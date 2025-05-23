// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.typespec.http.client.generator.core.template;

import com.microsoft.typespec.http.client.generator.core.extension.plugin.JavaSettings;
import com.microsoft.typespec.http.client.generator.core.model.projectmodel.Project;
import com.microsoft.typespec.http.client.generator.core.util.TemplateUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReadmeTemplate {

    public String write(Project project) {
        final String templateReadme
            = (!JavaSettings.getInstance().isUnbranded()) ? "Readme_protocol.txt" : "Readme_unbranded.txt";
        return TemplateUtil.loadTextFromResource(templateReadme, TemplateUtil.SERVICE_NAME, project.getServiceName(),
            TemplateUtil.SERVICE_DESCRIPTION, project.getServiceDescriptionForMarkdown(), TemplateUtil.GROUP_ID,
            project.getGroupId(), TemplateUtil.ARTIFACT_ID, project.getArtifactId(), TemplateUtil.ARTIFACT_VERSION,
            project.getVersion(), TemplateUtil.PACKAGE_NAME, project.getNamespace());
    }

    protected static String getImpression(Project project) {
        String impression = "";
        if (project.getSdkRepositoryPath().isPresent()) {
            try {
                impression = "![Impressions](https://azure-sdk-impressions.azurewebsites.net/api/impressions/"
                    + URLEncoder.encode("azure-sdk-for-java/" + project.getSdkRepositoryPath().get() + "/README.png",
                        StandardCharsets.UTF_8.name())
                    + ")";
            } catch (UnsupportedEncodingException e) {
                // NOOP
            }
        }
        return impression;
    }
}
