// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See License.txt in the project root for license information.

import {
  createLinterRuleTester,
  createTestRunner,
  type LinterRuleTester,
} from "@typespec/compiler/testing";
import { beforeEach, describe, it } from "vitest";
import { singleWordModelNameRule } from "../../../src/rules/single-word-model-name.rule.js";

describe("single-word-model-name rule", () => {
  let ruleTester: LinterRuleTester;

  beforeEach(async () => {
    const runner = await createTestRunner();
    ruleTester = createLinterRuleTester(
      runner,
      singleWordModelNameRule,
      "@typespec/http-client-csharp",
    );
  });

  describe("should flag single-word model names", () => {
    it("flags a simple single-word model name", async () => {
      await ruleTester.expect(`model Document {}`).toEmitDiagnostics({
        code: "@typespec/http-client-csharp/single-word-model-name",
        message:
          "Model name 'Document' is a single word. Consider using a more descriptive multi-word name to avoid naming collisions.",
      });
    });

    it("flags another single-word model name", async () => {
      await ruleTester.expect(`model Format { name: string; }`).toEmitDiagnostics({
        code: "@typespec/http-client-csharp/single-word-model-name",
        message:
          "Model name 'Format' is a single word. Consider using a more descriptive multi-word name to avoid naming collisions.",
      });
    });

    it("flags single-word model in a namespace", async () => {
      await ruleTester
        .expect(
          `
          namespace Azure.Storage.Tables {
            model Client {
              name: string;
            }
          }
        `,
        )
        .toEmitDiagnostics({
          code: "@typespec/http-client-csharp/single-word-model-name",
          message:
            "Model name 'Client' is a single word. Consider using a more descriptive multi-word name to avoid naming collisions.",
        });
    });
  });

  describe("should not flag multi-word model names", () => {
    it("allows two-word PascalCase model name", async () => {
      await ruleTester.expect(`model TableDocument {}`).toBeValid();
    });

    it("allows three-word PascalCase model name", async () => {
      await ruleTester.expect(`model AzureTableDocument {}`).toBeValid();
    });

    it("allows model name with acronym prefix", async () => {
      await ruleTester.expect(`model HTTPClient {}`).toBeValid();
    });

    it("allows model name ending with acronym", async () => {
      await ruleTester.expect(`model ConnectionTLS {}`).toBeValid();
    });
  });
});
