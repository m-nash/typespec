import vscode from "vscode";
import { sendLmChatRequest } from "../../lm/language-model.js";
import logger from "../../log/logger.js";

const SINGLE_WORD_DIAGNOSTIC_CODE = "@typespec/http-client-csharp/single-word-model-name";
const MODEL_FAMILY = "copilot-gpt-4.1";

type RenameApproach = "direct" | "clientName";

interface ModelNameContext {
  modelName: string;
  modelSource: string;
  namespaceName: string;
  documentUri: vscode.Uri;
  diagnosticRange: vscode.Range;
}

/**
 * Command that fetches AI name suggestions and applies the chosen rename.
 * The approach (direct vs clientName) is passed in — the user already picked
 * it from the Ctrl+. code action menu.
 */
export async function suggestModelName(
  document: vscode.TextDocument,
  diagnostic: vscode.Diagnostic,
  approach: RenameApproach,
): Promise<void> {
  const ctx = extractModelContext(document, diagnostic);
  if (!ctx) {
    vscode.window.showErrorMessage("Could not extract model information from the diagnostic.");
    return;
  }

  // Fetch AI suggestions with a progress notification
  const suggestions = await vscode.window.withProgress(
    {
      location: vscode.ProgressLocation.Notification,
      title: `Generating AI name suggestions for '${ctx.modelName}'...`,
      cancellable: true,
    },
    async (_progress, token) => {
      return await fetchAiSuggestions(ctx, token);
    },
  );

  if (!suggestions || suggestions.length === 0) {
    vscode.window.showWarningMessage(
      "Could not generate AI suggestions. Check that GitHub Copilot is active.",
    );
    return;
  }

  // Show QuickPick with AI suggestions
  const nameItems = suggestions.map((name, i) => ({
    label: name,
    description: i === 0 ? "(recommended)" : undefined,
  }));

  const titleMap: Record<string, string> = {
    direct: `Rename '${ctx.modelName}' to:`,
    clientName: `Add @@clientName override for '${ctx.modelName}':`,
    updateClientName: `Update client name for '${ctx.modelName}' to:`,
  };

  const selectedName = await vscode.window.showQuickPick(nameItems, {
    title: titleMap[approach] ?? `Choose new name for '${ctx.modelName}':`,
    placeHolder: "Select a suggested name",
  });

  if (!selectedName) return;

  if (approach === "direct") {
    await applyDirectRename(ctx, selectedName.label);
  } else if (approach === "updateClientName") {
    await applyUpdateClientName(document, diagnostic, selectedName.label);
  } else {
    await applyClientNameOverride(ctx, selectedName.label);
  }
}

function extractModelContext(
  document: vscode.TextDocument,
  diagnostic: vscode.Diagnostic,
): ModelNameContext | undefined {
  const range = diagnostic.range;
  const lineText = document.lineAt(range.start.line).text;

  // Check if this diagnostic is on a @@clientName line vs a model declaration
  const isOnClientNameLine = lineText.includes("@@clientName");

  let modelName: string;
  let modelSource: string;
  let namespaceName: string;

  if (isOnClientNameLine) {
    // Extract model name from @@clientName(ModelName, "...")
    const clientNameMatch = lineText.match(/@@clientName\(\s*(\w+)/);
    modelName = clientNameMatch ? clientNameMatch[1] : document.getText(range);

    // Find the actual model source in other open documents or imported files
    const modelInfo = findModelSourceAcrossFiles(modelName, document);
    modelSource = modelInfo.source;
    namespaceName = modelInfo.namespace;
  } else {
    modelName = document.getText(range);
    modelSource = extractModelSource(document, range);
    namespaceName = extractNamespace(document, range);
  }

  if (!modelName) return undefined;

  return {
    modelName,
    modelSource,
    namespaceName,
    documentUri: document.uri,
    diagnosticRange: range,
  };
}

/**
 * Searches for a model's source code across workspace files when the diagnostic
 * is on a @@clientName line (not the model declaration itself).
 */
function findModelSourceAcrossFiles(
  modelName: string,
  clientTspDocument: vscode.TextDocument,
): { source: string; namespace: string } {
  // Check all open text documents for the model declaration
  for (const doc of vscode.workspace.textDocuments) {
    if (doc.uri.fsPath === clientTspDocument.uri.fsPath) continue;
    if (!doc.uri.fsPath.endsWith(".tsp")) continue;

    const text = doc.getText();
    // Look for "model ModelName {" or "model ModelName " patterns
    const modelPattern = new RegExp(`model\\s+${modelName}\\s*[{<]`);
    const match = text.match(modelPattern);
    if (match && match.index !== undefined) {
      const pos = doc.positionAt(match.index);
      const range = new vscode.Range(pos, pos);
      return {
        source: extractModelSource(doc, range),
        namespace: extractNamespace(doc, range),
      };
    }
  }

  // Fallback: try the main.tsp in the same directory
  const dir = vscode.Uri.joinPath(clientTspDocument.uri, "..");
  const mainTspUri = vscode.Uri.joinPath(dir, "main.tsp");
  for (const doc of vscode.workspace.textDocuments) {
    if (doc.uri.toString() === mainTspUri.toString()) {
      const text = doc.getText();
      const modelPattern = new RegExp(`model\\s+${modelName}\\s*[{<]`);
      const match = text.match(modelPattern);
      if (match && match.index !== undefined) {
        const pos = doc.positionAt(match.index);
        const range = new vscode.Range(pos, pos);
        return {
          source: extractModelSource(doc, range),
          namespace: extractNamespace(doc, range),
        };
      }
    }
  }

  // Last resort: return minimal context
  return {
    source: `model ${modelName} {}`,
    namespace: extractNamespace(clientTspDocument, new vscode.Range(0, 0, 0, 0)),
  };
}

function extractModelSource(document: vscode.TextDocument, diagnosticRange: vscode.Range): string {
  // Walk backward to find the start of decorators/doc comments, forward to find closing brace
  const text = document.getText();
  let startOffset = document.offsetAt(diagnosticRange.start);

  // Walk back to find the line with 'model' keyword or decorators
  while (startOffset > 0 && text[startOffset - 1] !== "\n") {
    startOffset--;
  }
  // Continue walking back through decorator lines
  while (startOffset > 0) {
    const prevLineStart = text.lastIndexOf("\n", startOffset - 2) + 1;
    const prevLine = text.slice(prevLineStart, startOffset).trim();
    if (prevLine.startsWith("@") || prevLine.startsWith("*") || prevLine.startsWith("/**")) {
      startOffset = prevLineStart;
    } else {
      break;
    }
  }

  // Walk forward to find the matching closing brace
  let braceCount = 0;
  let endOffset = document.offsetAt(diagnosticRange.end);
  let foundOpen = false;
  while (endOffset < text.length) {
    if (text[endOffset] === "{") {
      braceCount++;
      foundOpen = true;
    } else if (text[endOffset] === "}") {
      braceCount--;
      if (foundOpen && braceCount === 0) {
        endOffset++;
        break;
      }
    }
    endOffset++;
  }

  return text.slice(startOffset, endOffset);
}

function extractNamespace(document: vscode.TextDocument, range: vscode.Range): string {
  const text = document.getText();
  const offset = document.offsetAt(range.start);
  const textBefore = text.slice(0, offset);

  // Simple regex to find the last namespace declaration
  const nsMatch = textBefore.match(/namespace\s+([\w.]+)\s*[{;]/g);
  if (nsMatch && nsMatch.length > 0) {
    const last = nsMatch[nsMatch.length - 1];
    const nameMatch = last.match(/namespace\s+([\w.]+)/);
    return nameMatch ? nameMatch[1] : "";
  }
  return "";
}

async function fetchAiSuggestions(
  ctx: ModelNameContext,
  token?: vscode.CancellationToken,
): Promise<string[]> {
  const prompt = `You are a .NET naming expert for Azure SDKs. Given the following TypeSpec model, suggest exactly 5 better multi-word names.

Requirements:
- Each name MUST be 2+ words in PascalCase (e.g., "StorageDocument" not just "Document")
- A key goal is UNIQUENESS across Azure services — the name must not conflict with models of the same name in other Azure services (e.g., "Document" exists in Cosmos DB, AI Document Intelligence, Search, etc.)
- Names should describe the model's PURPOSE based on its properties, doc comments, and context
- The namespace can inform your suggestions but should not be the only source of inspiration — prioritize what the model represents functionally
- Order by confidence: put your best suggestion first, then descending
- Follow .NET and Azure SDK naming conventions

Namespace: ${ctx.namespaceName}
Current name: ${ctx.modelName}

Return ONLY the 5 names, one per line. No explanations, no numbering, no backticks.

\`\`\`tsp
${ctx.modelSource}
\`\`\``;

  try {
    // Try multiple model families — availability varies by Copilot version
    const families = ["gpt-4o", "gpt-4", "gpt-3.5-turbo", "copilot-gpt-4.1"];
    let response: string | undefined;

    for (const family of families) {
      if (token?.isCancellationRequested) return [];
      try {
        logger.info(`Trying LM model family: ${family}`);
        response = await sendLmChatRequest(
          [{ role: "user", message: prompt }],
          family,
          undefined,
          `suggest-model-name-${ctx.modelName}`,
        );
        if (response) break;
      } catch {
        // Try next family
      }
    }

    if (!response) {
      logger.warning("No LM model responded for model name suggestions");
      return [];
    }

    logger.info(`AI raw response for '${ctx.modelName}': ${response}`);

    return response
      .split(/[\r\n]+/)
      .map((line) => {
        // Strip numbering (e.g., "1. FooBar", "1) FooBar", "- FooBar")
        let cleaned = line.trim().replace(/^[\d]+[.)]\s*/, "").replace(/^[-*•]\s*/, "");
        // Strip backticks and quotes
        cleaned = cleaned.replace(/[`'"]/g, "").trim();
        return cleaned;
      })
      .filter((line) => line.length > 0 && /^[A-Z][A-Za-z0-9_]*$/.test(line))
      .slice(0, 5);
  } catch (e) {
    logger.error("Failed to get AI suggestions for model name", [e]);
    return [];
  }
}

async function applyDirectRename(ctx: ModelNameContext, newName: string): Promise<void> {
  // Use the language server's rename provider to find and update all references
  const position = ctx.diagnosticRange.start;

  try {
    const workspaceEdit = await vscode.commands.executeCommand<vscode.WorkspaceEdit>(
      "vscode.executeDocumentRenameProvider",
      ctx.documentUri,
      position,
      newName,
    );

    if (workspaceEdit && workspaceEdit.size > 0) {
      const success = await vscode.workspace.applyEdit(workspaceEdit);
      if (success) {
        const entryCount = [...workspaceEdit.entries()].reduce((sum, [, edits]) => sum + edits.length, 0);
        vscode.window.showInformationMessage(
          `Renamed '${ctx.modelName}' to '${newName}' (${entryCount} reference${entryCount > 1 ? "s" : ""} updated)`,
        );
      } else {
        vscode.window.showErrorMessage(`Failed to apply rename`);
      }
    } else {
      // Fallback: simple text replacement if rename provider returns nothing
      const edit = new vscode.WorkspaceEdit();
      edit.replace(ctx.documentUri, ctx.diagnosticRange, newName);
      await vscode.workspace.applyEdit(edit);
      vscode.window.showInformationMessage(`Renamed model to '${newName}'`);
    }
  } catch (e) {
    // Fallback if rename provider fails
    logger.warning(`Rename provider failed, falling back to simple replace: ${e}`);
    const edit = new vscode.WorkspaceEdit();
    edit.replace(ctx.documentUri, ctx.diagnosticRange, newName);
    const success = await vscode.workspace.applyEdit(edit);
    if (success) {
      vscode.window.showInformationMessage(`Renamed model to '${newName}' (references may need manual update)`);
    } else {
      vscode.window.showErrorMessage(`Failed to rename model`);
    }
  }
}

/**
 * Updates the name string in an existing @@clientName decorator.
 * If the diagnostic is on the @@clientName line itself, edits in place.
 * If the diagnostic is on the model line (auto-discovered client.tsp), finds client.tsp and edits there.
 */
async function applyUpdateClientName(
  document: vscode.TextDocument,
  diagnostic: vscode.Diagnostic,
  newName: string,
): Promise<void> {
  const lineText = document.lineAt(diagnostic.range.start.line).text;

  // Check if we're on a @@clientName line directly
  if (lineText.includes("@@clientName")) {
    await replaceClientNameInLine(document.uri, diagnostic.range.start.line, newName);
    return;
  }

  // Auto-discovered case: diagnostic is on the model line, find client.tsp
  const modelName = document.getText(diagnostic.range);
  const docDir = vscode.Uri.joinPath(document.uri, "..");
  const clientTspUri = vscode.Uri.joinPath(docDir, "client.tsp");

  try {
    const clientDoc = await vscode.workspace.openTextDocument(clientTspUri);
    // Find the @@clientName line for this model
    for (let i = 0; i < clientDoc.lineCount; i++) {
      const line = clientDoc.lineAt(i).text;
      if (line.includes("@@clientName") && line.includes(modelName)) {
        await replaceClientNameInLine(clientTspUri, i, newName);
        // Show the edited file
        await vscode.window.showTextDocument(clientDoc, { preview: true, preserveFocus: true });
        return;
      }
    }
    vscode.window.showErrorMessage(
      `Could not find @@clientName for '${modelName}' in client.tsp`,
    );
  } catch {
    vscode.window.showErrorMessage(`Could not open client.tsp`);
  }
}

async function replaceClientNameInLine(
  uri: vscode.Uri,
  lineNumber: number,
  newName: string,
): Promise<void> {
  const doc = await vscode.workspace.openTextDocument(uri);
  const lineText = doc.lineAt(lineNumber).text;

  const match = lineText.match(/,\s*"([^"]+)"/);
  if (!match || match.index === undefined) {
    vscode.window.showErrorMessage("Could not find the name string in the @@clientName decorator.");
    return;
  }

  const quoteStart = lineText.indexOf('"', match.index) + 1;
  const quoteEnd = lineText.indexOf('"', quoteStart);
  const range = new vscode.Range(lineNumber, quoteStart, lineNumber, quoteEnd);

  const edit = new vscode.WorkspaceEdit();
  edit.replace(uri, range, newName);
  const success = await vscode.workspace.applyEdit(edit);

  if (success) {
    vscode.window.showInformationMessage(`Updated client name to '${newName}'`);
  } else {
    vscode.window.showErrorMessage(`Failed to update client name`);
  }
}

async function applyClientNameOverride(ctx: ModelNameContext, newName: string): Promise<void> {
  const docDir = vscode.Uri.joinPath(ctx.documentUri, "..");
  const clientTspUri = vscode.Uri.joinPath(docDir, "client.tsp");

  const overrideLine = `@@clientName(${ctx.modelName}, "${newName}");`;

  let fileExists = false;
  let existingContent = "";
  try {
    // Read from the editor buffer if open, otherwise from disk
    const openDoc = vscode.workspace.textDocuments.find(
      (d) => d.uri.toString() === clientTspUri.toString(),
    );
    if (openDoc) {
      existingContent = openDoc.getText();
    } else {
      const raw = await vscode.workspace.fs.readFile(clientTspUri);
      existingContent = new TextDecoder().decode(raw);
    }
    fileExists = true;
  } catch {
    // File doesn't exist
  }

  if (existingContent.includes(overrideLine)) {
    vscode.window.showInformationMessage(`Override already exists in client.tsp`);
    return;
  }

  const edit = new vscode.WorkspaceEdit();

  if (!fileExists) {
    // Create new file via WorkspaceEdit
    const mainFileName = ctx.documentUri.path.split("/").pop() ?? "main.tsp";
    const lines = [
      `import "@azure-tools/typespec-client-generator-core";`,
      `import "./${mainFileName}";`,
      ``,
      `using Azure.ClientGenerator.Core;`,
    ];
    if (ctx.namespaceName) {
      lines.push(`using ${ctx.namespaceName};`);
    }
    lines.push(``, overrideLine, ``);

    edit.createFile(clientTspUri, { ignoreIfExists: true });
    edit.insert(clientTspUri, new vscode.Position(0, 0), lines.join("\n"));
  } else {
    // Append to existing file via WorkspaceEdit (respects unsaved buffers)
    const openDoc =
      vscode.workspace.textDocuments.find(
        (d) => d.uri.toString() === clientTspUri.toString(),
      ) ?? (await vscode.workspace.openTextDocument(clientTspUri));

    // Add using if needed
    if (ctx.namespaceName && !existingContent.includes(`using ${ctx.namespaceName};`)) {
      const lastUsingLine = findLastLineMatching(openDoc, /^using\s/);
      if (lastUsingLine >= 0) {
        const insertPos = new vscode.Position(lastUsingLine + 1, 0);
        edit.insert(clientTspUri, insertPos, `using ${ctx.namespaceName};\n`);
      }
    }

    // Insert after the last existing @@clientName line, or at end with a blank line
    const lastClientNameLine = findLastLineMatching(openDoc, /^@@clientName\(/);
    if (lastClientNameLine >= 0) {
      // Append right after the last @@clientName line
      const insertPos = new vscode.Position(lastClientNameLine + 1, 0);
      edit.insert(clientTspUri, insertPos, `${overrideLine}\n`);
    } else {
      // No existing @@clientName lines — append at end with a blank line separator
      const lastLine = openDoc.lineCount - 1;
      const lastChar = openDoc.lineAt(lastLine).text.length;
      const endPos = new vscode.Position(lastLine, lastChar);
      const separator = existingContent.endsWith("\n") ? "" : "\n";
      edit.insert(clientTspUri, endPos, `${separator}\n${overrideLine}\n`);
    }
  }

  const success = await vscode.workspace.applyEdit(edit);
  if (!success) {
    vscode.window.showErrorMessage("Failed to update client.tsp");
    return;
  }

  // Ensure tspconfig.yaml imports client.tsp
  await ensureClientTspImport(docDir);

  vscode.window.showInformationMessage(
    `Added @@clientName override to client.tsp: ${ctx.modelName} → ${newName}`,
  );

  const doc = await vscode.workspace.openTextDocument(clientTspUri);
  await vscode.window.showTextDocument(doc, { preview: true, preserveFocus: true });
}

function findLastLineMatching(doc: vscode.TextDocument, pattern: RegExp): number {
  for (let i = doc.lineCount - 1; i >= 0; i--) {
    if (pattern.test(doc.lineAt(i).text)) return i;
  }
  return -1;
}

/**
 * Ensures tspconfig.yaml has `imports: - ./client.tsp` so client.tsp is part of compilation.
 */
async function ensureClientTspImport(projectDir: vscode.Uri): Promise<void> {
  const tspConfigUri = vscode.Uri.joinPath(projectDir, "tspconfig.yaml");

  let content: string;
  try {
    const raw = await vscode.workspace.fs.readFile(tspConfigUri);
    content = new TextDecoder().decode(raw);
  } catch {
    return; // No tspconfig.yaml — nothing to do
  }

  const importLine = "./client.tsp";

  // Check if already imported
  if (content.includes(importLine)) {
    return;
  }

  // Add imports section or append to existing one
  const importsMatch = content.match(/^imports:\s*$/m);
  if (importsMatch && importsMatch.index !== undefined) {
    // imports: section exists but doesn't have client.tsp — append to it
    const insertPos = importsMatch.index + importsMatch[0].length;
    content = content.slice(0, insertPos) + `\n  - ${importLine}` + content.slice(insertPos);
  } else if (content.match(/^imports:/m)) {
    // imports: section exists with items — append after last import line
    const lines = content.split("\n");
    let lastImportLine = -1;
    let inImports = false;
    for (let i = 0; i < lines.length; i++) {
      if (/^imports:/.test(lines[i])) {
        inImports = true;
        lastImportLine = i;
      } else if (inImports && /^\s+-\s/.test(lines[i])) {
        lastImportLine = i;
      } else if (inImports && !/^\s*$/.test(lines[i])) {
        break;
      }
    }
    if (lastImportLine >= 0) {
      lines.splice(lastImportLine + 1, 0, `  - ${importLine}`);
      content = lines.join("\n");
    }
  } else {
    // No imports section — add one at the top
    content = `imports:\n  - ${importLine}\n` + content;
  }

  await vscode.workspace.fs.writeFile(tspConfigUri, new TextEncoder().encode(content));
}

/**
 * Check if a diagnostic is a single-word model name warning from the C# emitter.
 */
export function isSingleWordModelNameDiagnostic(diagnostic: vscode.Diagnostic): boolean {
  if (diagnostic.source !== "TypeSpec") return false;
  const code = diagnostic.code;
  if (!code) return false;
  // The code can be a string or an object with a "value" property
  if (typeof code === "string") return code === SINGLE_WORD_DIAGNOSTIC_CODE;
  if (typeof code === "object" && "value" in code) {
    return String(code.value) === SINGLE_WORD_DIAGNOSTIC_CODE;
  }
  return false;
}
