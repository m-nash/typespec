import type { AreaLabels } from "./labels.js";

/**
 * Set the paths that each area applies to.
 */
export const AreaPaths: Record<keyof typeof AreaLabels, string[]> = {
  "compiler:core": ["packages/compiler/"],
  "compiler:emitter-framework": [],
  ide: ["packages/typespec-vscode/", "packages/typespec-vs/"],
  "lib:http": ["packages/http/"],
  "lib:openapi": ["packages/openapi/"],
  "lib:rest": ["packages/rest/"],
  "lib:versioning": ["packages/versioning/"],
  "lib:http-specs": ["packages/http-specs/"],
  "meta:blog": ["blog/"],
  "meta:website": ["website/"],
  tspd: ["packages/tspd/"],
  "emitter:client:all": [],
  "emitter:client:js": ["packages/http-client-js/"],
  "emitter:client:csharp": ["packages/http-client-csharp/"],
  "emitter:client:java": ["packages/http-client-java/"],
  "emitter:client:python": ["packages/http-client-python/"],
  "emitter:graphql": ["packages/graphql/"],
  "emitter:json-schema": ["packages/json-schema/"],
  "emitter:protobuf": ["packages/protobuf/"],
  "emitter:openapi3": ["packages/openapi3/"],
  "openapi3:converter": ["packages/openapi3/src/cli/actions/convert/"],
  "emitter:service:csharp": ["packages/http-server-csharp"],
  "emitter:service:js": ["packages/http-server-js"],
  "emitter:service:java": [],
  eng: ["eng/", ".github/"],
  "ui:playground": ["packages/playground/"],
  "ui:type-graph-viewer": ["packages/html-program-viewer/"],
  spector: ["packages/spector/", "packages/http-specs"],
};

/**
 * Path that should trigger every CI build.
 */
const all = ["eng/common/", "vitest.config.ts", "tsconfig.base.json"];

/**
 * Path that should trigger all isolated emitter builds
 */
const isolatedEmitters = ["eng/emitters/"];

export const CIRules = {
  CSharp: [...all, ...isolatedEmitters, ...AreaPaths["emitter:client:csharp"], ".editorconfig"],
  Java: [...all, ...isolatedEmitters, ...AreaPaths["emitter:client:java"], ".editorconfig"],
  Python: [...all, ...isolatedEmitters, ...AreaPaths["emitter:client:python"], ".editorconfig"],

  Core: [
    "**/*",
    "!.prettierignore", // Prettier is already run as its dedicated CI(via github action)
    "!.prettierrc.json",
    "!cspell.yaml", // CSpell is already run as its dedicated CI(via github action)
    "!eslint.config.json", // Eslint is already run as its dedicated CI(via github action)
    ...ignore(isolatedEmitters),
    ...ignore(AreaPaths["emitter:client:csharp"]),
    ...ignore(AreaPaths["emitter:client:java"]),
    ...ignore(AreaPaths["emitter:client:python"]),
  ],
};

function ignore(paths: string[]) {
  return paths.map((x) => `!${x}`);
}
