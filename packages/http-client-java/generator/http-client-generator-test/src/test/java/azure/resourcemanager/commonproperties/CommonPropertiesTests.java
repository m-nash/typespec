// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package azure.resourcemanager.commonproperties;

import azure.resourcemanager.commonproperties.models.ApiErrorException;
import azure.resourcemanager.commonproperties.models.ConfidentialResourceProperties;
import azure.resourcemanager.commonproperties.models.ManagedIdentityTrackedResource;
import azure.resourcemanager.commonproperties.models.ManagedIdentityTrackedResourceProperties;
import azure.resourcemanager.commonproperties.models.ManagedServiceIdentity;
import azure.resourcemanager.commonproperties.models.ManagedServiceIdentityType;
import azure.resourcemanager.commonproperties.models.UserAssignedIdentity;
import com.azure.core.management.Region;
import com.azure.core.management.exception.ManagementException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.utils.ArmUtils;

public class CommonPropertiesTests {
    private static final String USER_ASSIGNED_IDENTITIES_KEY
        = "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/test-rg/providers/Microsoft.ManagedIdentity/userAssignedIdentities/id1";
    private final CommonPropertiesManager manager
        = CommonPropertiesManager.authenticate(ArmUtils.createTestHttpPipeline(), ArmUtils.getAzureProfile());

    @Test
    public void testManagedIdentity() {
        Map<String, String> tagsMap = new HashMap<>();
        tagsMap.put("tagKey1", "tagValue1");
        ManagedIdentityTrackedResource resource = manager.managedIdentities()
            .define("identity")
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup("test-rg")
            .withProperties(new ManagedIdentityTrackedResourceProperties())
            .withIdentity(new ManagedServiceIdentity().withType(ManagedServiceIdentityType.SYSTEM_ASSIGNED))
            .withTags(tagsMap)
            .create();
        Assertions.assertEquals(ManagedServiceIdentityType.SYSTEM_ASSIGNED, resource.identity().type());
        Assertions.assertNotNull(resource.identity().principalId());
        Assertions.assertNotNull(resource.identity().tenantId());

        resource = manager.managedIdentities().getById(resource.id());
        Assertions.assertEquals(ManagedServiceIdentityType.SYSTEM_ASSIGNED, resource.identity().type());
        Assertions.assertNotNull(resource.identity().principalId());
        Assertions.assertNotNull(resource.identity().tenantId());

        Map<String, UserAssignedIdentity> userAssignedIdentityMap = new HashMap<>();
        userAssignedIdentityMap.put(USER_ASSIGNED_IDENTITIES_KEY, new UserAssignedIdentity());
        resource.update()
            .withIdentity(
                new ManagedServiceIdentity().withType(ManagedServiceIdentityType.SYSTEM_ASSIGNED_USER_ASSIGNED)
                    .withUserAssignedIdentities(userAssignedIdentityMap))
            .apply();
        Assertions.assertEquals(ManagedServiceIdentityType.SYSTEM_ASSIGNED_USER_ASSIGNED, resource.identity().type());
        Assertions.assertNotNull(resource.identity().principalId());
        Assertions.assertNotNull(resource.identity().tenantId());
        Assertions.assertNotNull(resource.identity().userAssignedIdentities());
        Assertions.assertEquals(1, resource.identity().userAssignedIdentities().size());
        UserAssignedIdentity userAssignedIdentity
            = resource.identity().userAssignedIdentities().get(USER_ASSIGNED_IDENTITIES_KEY);
        Assertions.assertNotNull(userAssignedIdentity.principalId());
        Assertions.assertNotNull(userAssignedIdentity.clientId());

    }

    @Test
    public void testError() {
        ApiErrorException apiErrorException = null;
        try {
            manager.errors()
                .define("confidential")
                .withRegion(Region.US_EAST)
                .withExistingResourceGroup("test-rg")
                .withProperties(new ConfidentialResourceProperties().withUsername("00"))
                .create();
        } catch (ApiErrorException e) {
            apiErrorException = e;
        }
        Assertions.assertNotNull(apiErrorException);
        Assertions.assertEquals("BadRequest", apiErrorException.getValue().getCode());
        Assertions.assertEquals("Username should not contain only numbers.", apiErrorException.getValue().getMessage());
        Assertions.assertEquals("general", apiErrorException.getValue().getInnererror().exceptiontype());

        ManagementException exception = null;
        try {
            manager.errors().getByResourceGroup("test-rg", "confidential");
        } catch (ManagementException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("ResourceNotFound", exception.getValue().getCode());
    }
}
