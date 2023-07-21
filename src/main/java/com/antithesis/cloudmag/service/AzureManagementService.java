package com.antithesis.cloudmag.service;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.PublicIpAddressSku;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AzureManagementService {

    private static final String REGION = "eastus";

    private static final String GROUP_NAME = "tesis-resource-groups";

    public AzureManagementService() {
    }

    public VirtualMachine createVirtualMachine(String name) {
        // TODO
        TokenCredential credential = new EnvironmentCredentialBuilder()
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();

        // Please finish 'Set up authentication' step first to set the four environment variables: AZURE_SUBSCRIPTION_ID, AZURE_CLIENT_ID, AZURE_CLIENT_SECRET, AZURE_TENANT_ID
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withDefaultSubscription();

        return azureResourceManager.virtualMachines()
                .define(name)
                .withRegion(REGION)
                .withNewResourceGroup()
                .withNewPrimaryNetwork("10.0.0.0/24")
                .withPrimaryPrivateIPAddressDynamic()
                .withNewPrimaryPublicIPAddress(name + "-ip")
                .withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_20_04_LTS)
                .withRootUsername("testUser")
                .withSsh("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC7x6dZvODwhSV+SplyrDFpKMU98dSlfWEkRJUdZ6HbbHAARS2uvWBQ/dqO83AcvvvZABj8xeyxAjMWVebXb6daCPYya051PvufWA/O8FDIvo1NsAlcB0r4IldbsofrETnAfBJXGxqnS5LV8x1N3PWfAVus4OFaO0RPTwH27BFSHDtuWJvww8/GjzckJdb8xWJq7jNoReJXnyWDYUC+hjvMEiGQEb5nwcsDK4FwFfaXTuuHvMpldmNji6QAQg6BdGcAKsvWpAP/NPrae0gEio11DqMo6ONbUS2mAwu05rDsbLLKgXMaUiZHIlPq0s+TKAJ8sUiNtSB9niDp897j54YZF0lca9/eoJnqeDQHoFjdsYj6woPc1OcB6kVa1B9RVxmUEmQVoiwRQ2Heesqkrbote2LHZBDWnpdD07/GSr1/EM4kIdPDiclMRv7kgnF05Q02AJ8SypT0Cot2XH2FAMEfzH0zQlMRpbwgLlvbEVK3QfBbLgANiPRYu8Blw2xYqyL+7iT9asQ0XTwr315jx+x2xNzJ3Ek8mlxPQ17+Et3EIkNycxEyO1iGQGJv9ospzVX3l2wT7Ymc8Augucq3V2EBjAljAIFlVAB5ztjmOlxXONnay1YBeqkOF237LhM/pQHq09bZhEedMZJVQl9i5BcXfjd8DTPY4ET54j5v43dMJQ== -anfelpe.0200@gmail.com")
                .withSize(VirtualMachineSizeTypes.STANDARD_D2S_V3)
                .create();
    }
}
