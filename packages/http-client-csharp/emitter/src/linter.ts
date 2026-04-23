// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See License.txt in the project root for license information.

import { defineLinter } from "@typespec/compiler";
import { singleWordModelNameRule } from "./rules/index.js";

export const $linter = defineLinter({
  rules: [singleWordModelNameRule],
  ruleSets: {
    recommended: {
      enable: {
        [`@typespec/http-client-csharp/${singleWordModelNameRule.name}`]: true,
      },
    },
  },
});
