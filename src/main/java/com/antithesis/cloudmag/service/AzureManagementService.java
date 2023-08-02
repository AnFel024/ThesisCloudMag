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
import com.azure.resourcemanager.network.models.PublicIpAddress;
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
                .withExistingResourceGroup(GROUP_NAME)
                .withNewPrimaryNetwork("10.0.0.0/24")
                .withPrimaryPrivateIPAddressDynamic()
                .withNewPrimaryPublicIPAddress(name + "-ip")
                .withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_20_04_LTS)
                .withRootUsername("testUser")
                .withSsh("ecdsa-sha2-nistp521 AAAAE2VjZHNhLXNoYTItbmlzdHA1MjEAAAAIbmlzdHA1MjEAAACFBACTxswppM9TgmAAe4ysKVu0h9Y7T8LoX4G6WSkSuLfkM454o/h+uX7DOJonNa/JfGErGma1h3aGUCQ4rp9COV/5fgEiJqkDRxJyI2WL+Xn49rWLNy/Qo5pgRfzAvuQgW15DwUkHI/6uGFdCP+dxPpnwp05KzO6UG0QUiVMfKJntYAnbYg== anfelpe.0200@gmail.com")
                .withSize(VirtualMachineSizeTypes.STANDARD_D2S_V3)
                .create();
    }
}
