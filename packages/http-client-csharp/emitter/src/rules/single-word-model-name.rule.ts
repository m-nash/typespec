// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See License.txt in the project root for license information.

import { createRule, getSourceLocation, type Model, paramMessage } from "@typespec/compiler";

export const singleWordModelNameRule = createRule({
  name: "single-word-model-name",
  severity: "warning",
  description:
    "Model names should be multi-word to avoid naming collisions with BCL or third-party types.",
  messages: {
    default: paramMessage`Model name '${"modelName"}' is a single word. Consider using a more descriptive multi-word name to avoid naming collisions.`,
    clientNameSingleWord: paramMessage`Client name override '${"newName"}' for model '${"modelName"}' is still a single word. Use a multi-word name to avoid naming collisions.`,
  },
  create(context) {
    return {
      model: (model: Model) => {
        if (!isModelStatement(model)) {
          return;
        }

        // Check if model has a @clientName / @@clientName decorator
        const decoratorOverride = getClientNameFromDecorators(model);
        if (decoratorOverride !== undefined) {
          if (isSingleWord(decoratorOverride.name)) {
            // Flag at the decorator node so Ctrl+. targets the override
            const target = decoratorOverride.decoratorNode ?? model;
            context.reportDiagnostic({
              messageId: "clientNameSingleWord",
              format: { newName: decoratorOverride.name, modelName: model.name },
              target,
            });
          }
          // Has override (multi-word) — model is fine
          return;
        }

        // No override — flag if single-word
        if (isSingleWord(model.name)) {
          context.reportDiagnostic({
            messageId: "default",
            format: { modelName: model.name },
            target: model,
          });
        }
      },
    };
  },
});

interface ClientNameInfo {
  name: string;
  decoratorNode?: any;
}

function getClientNameFromDecorators(model: Model): ClientNameInfo | undefined {
  for (const dec of model.decorators) {
    const defName = dec.definition?.name;
    if (defName?.endsWith("clientName")) {
      for (const arg of dec.args ?? []) {
        if (typeof arg.jsValue === "string") {
          return {
            name: arg.jsValue,
            decoratorNode: dec.node,
          };
        }
      }
    }
  }
  return undefined;
}

/**
 * Checks if a model is an explicit model statement (not expression, intersection, etc.)
 * and is in user source (not node_modules or built-in).
 */
function isModelStatement(model: Model): boolean {
  const name = model.name;
  if (!name || name === "" || name.startsWith("_")) return false;
  if (!model.node) return false;
  if (model.templateMapper !== undefined) return false;
  if (!("id" in model.node) || !(model.node as any).id) return false;

  const location = getSourceLocation(model.node);
  if (location && location.file?.path?.includes("node_modules")) return false;

  return true;
}

/**
 * Checks if a name is a single PascalCase word.
 * Single-word: "Document", "Format", "Client"
 * Multi-word: "TableDocument", "BlobFormat", "HttpClient"
 */
function isSingleWord(name: string): boolean {
  if (name.length <= 1) return false;

  // Must start with uppercase
  if (!/^[A-Z]/.test(name)) return false;

  // Split on uppercase boundaries: "FooBar" → ["Foo", "Bar"]
  const segments = name.split(/(?=[A-Z])/);

  // If there's only one segment, it's a single word
  // But allow known acronym patterns (e.g., "HTTP" is one segment but OK)
  if (segments.length <= 1) return true;

  // Filter out single-char segments from acronym runs
  // "HTTPClient" → segments ["H", "T", "T", "P", "Client"] → meaningful: ["Client"] + acronym prefix
  // We need a smarter split: find runs of uppercase + a lowercase-started word
  const words = splitPascalCase(name);
  return words.length <= 1;
}

/**
 * Splits a PascalCase name into logical words, handling acronyms.
 * "TableDocument" → ["Table", "Document"]
 * "HTTPClient" → ["HTTP", "Client"]
 * "Document" → ["Document"]
 * "ID" → ["ID"]
 */
function splitPascalCase(name: string): string[] {
  const words: string[] = [];
  let current = "";

  for (let i = 0; i < name.length; i++) {
    const char = name[i];
    const isUpper = char >= "A" && char <= "Z";
    const nextIsLower =
      i + 1 < name.length && name[i + 1] >= "a" && name[i + 1] <= "z";

    if (isUpper && current.length > 0) {
      // Start of a new word if:
      // 1. Previous was lowercase (e.g., "Table|D")
      // 2. Current is uppercase and next is lowercase, and previous was uppercase
      //    (e.g., "HTT|P|Client" → "HTTP" boundary before "Client")
      const prevIsLower =
        current[current.length - 1] >= "a" && current[current.length - 1] <= "z";

      if (prevIsLower) {
        words.push(current);
        current = char;
      } else if (nextIsLower && current.length > 0) {
        // Acronym boundary: "HTTP|Client"
        words.push(current);
        current = char;
      } else {
        current += char;
      }
    } else {
      current += char;
    }
  }

  if (current.length > 0) {
    words.push(current);
  }

  return words;
}
