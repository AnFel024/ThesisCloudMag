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
                .withSsh("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCnFXbFMyHATHJKisQFtD2a9W9fgbb+ucdwle84ma8GLa0LVvBOkHDvX81a9wzUBd5YSyq3JlIvZ80sXoj1HB10PHk1Bwzic60gGY8KoBruzlpGtM06Z94Opws/PZa6aVm5gGdoiQ2n73BNQ2kxcAusyokdKV26yrlFn57nc+LgWclCDwxdUrVdETLufsICr3YHfexkL6Omaie5m/xebFvW180giTQclyI1RQQJd07fHAaJvDK4XXEipcZyUeKOREZn0B5sXf56ehgZ8djhpUpOzea5Zv+MoM4joRjwsyd+hpvOgCnjs1OiYUzYhL136NmhWqPxvEvTJEkJPFzgwNbPBZBD68T6l9pju+RBLam0fseOXaAriMf0Ko1F1bAJ1wx2sBqGrxU6yUm/QWcHRl9FvbUK4K09inwmmLEDQ07zE86yRGtHOd0jtxnZJGCL3VcJGm2JOArseFarkILgJ9g+NpWNfU9y4rDkNPd5WA/4bXLdmtsl8kyDiX2MNQnTmas2xre7hNzmrDYg+dES3j3tI00BToNcNVb6FwcPr0d24hyoQxgDkDWqw7jxE5oP7qi0c5zcJwR5IVt3hekgUkwEvOERbS7zEoTT3bxpZ26kDLSj0HIqRjDdTDcUT0B11yHOByqzHpb0zvCVbIGrP9PwJP8xx5fIA1OHY3GruKfe7w== anfelpe.0200@gmail.com")
                .withSize(VirtualMachineSizeTypes.STANDARD_D2S_V3)
                .create();
    }
}
