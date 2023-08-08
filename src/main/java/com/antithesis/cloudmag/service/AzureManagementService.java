package com.antithesis.cloudmag.service;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import com.azure.resourcemanager.network.models.NetworkSecurityGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Named;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AzureManagementService {

    private static final String REGION = "eastus";

    private static final String GROUP_NAME = "tesis-resource-groups";

    private final String sshPublicKey;
    private final AzureResourceManager azureResourceManager;

    public AzureManagementService(@Qualifier("azure-vms-configuration") AzureResourceManager azureResourceManager,
                                  @Value("${ssh.public.key}") String sshPublicKey) {
        this.azureResourceManager = azureResourceManager;
        this.sshPublicKey = sshPublicKey;
    }

    public VirtualMachine createVirtualMachine(String name) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines()
                .define(name)
                .withRegion(REGION)
                .withExistingResourceGroup(GROUP_NAME)
                .withNewPrimaryNetwork("10.0.0.0/24")
                .withPrimaryPrivateIPAddressDynamic()
                .withNewPrimaryPublicIPAddress(name + "-ip")
                .withPopularLinuxImage(KnownLinuxVirtualMachineImage.UBUNTU_SERVER_20_04_LTS)
                .withRootUsername("ubuntu")
                .withSsh("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDVBBWMIO59CwUpdPmEBjueoiS/vp8qe4oOW+Qnf6Eees2p5K5XxvOFMmTVN7QcjGR0K6bK0H6oj2NL6l1JJTNWYDNRM6kPIY6qCyw/Gbs6HJyb7/ypPfJUF1X/pUkjrK+McuxhKFNVr4JbJLdx91ZmmZJ5+P2aVL+qrANrFKY1ZE1Kl2k744J7Xa+B8kv/EUENpmRnr5sj47U4uExuRMGzdRasbBz6M/AfdDokE427VXBybh00JIzYZtOl9/hD42JySg2mXM3GNssirrNJvSAfmvWm/IMFCDw8XrSJkv3YretL5U+riT/OfXddRz0wLQAbT13QsjZnWc7KEVHz8nRVxZ3nz/dFGn3FQ4morKsDkqxA5UlD9Fa09myqItCDy+8ElhJ3REhYCOGmGjOK/nVVLsgIgRQW3wcauB/QsjHHK7H1TvzGFOL6XqCnqzly0g/mTWDAh0kwdyT3YJ22sejS/0Df/eLH/aKJccmsamwmVsrg9VMqYduLQ64egA+BVgYkvld8ixmPIop9NlyPDC/5haM6KEUC7TPweIrXCXgKP2c8raQsXhblROjWg9WG72AESATfRUlmrzqLO8Tqunggnoussx1/MeaOdJCTulIUdX/Bdj1xjaP7aFSCBK19GmZEHRjDRYE/DQ8ZzAqCJ5UrFYmpfpUd0lI8ADOmBJ22ow== anfelpe.0200@gmail.com")
                .withSize(VirtualMachineSizeTypes.STANDARD_D2S_V3)
                .create();
        NetworkSecurityGroup grupo1 = azureResourceManager.networkSecurityGroups().getByResourceGroup(GROUP_NAME, "Grupo1");
        virtualMachine.getPrimaryNetworkInterface().update()
                .withExistingNetworkSecurityGroup(grupo1)
                .apply();
        return virtualMachine;
    }

    public boolean deleteVirtualMachine(String id) {
        try {
            VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getByResourceGroup(GROUP_NAME, id);
            virtualMachine.deallocate();
            CompletableFuture<Void> voidCompletableFuture = deletePublicIpAddress(virtualMachine);
            CompletableFuture<Void> future = azureResourceManager.virtualMachines().deleteByIdAsync(virtualMachine.id(), true).toFuture();
            voidCompletableFuture.thenCombine(future, (unused, unused2) -> {
                log.info("Virtual machine deleted");
                return true;
            }).join();
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }
        return true;
    }

    public CompletableFuture<Void> deletePublicIpAddress(VirtualMachine virtualMachine) {
        String networkId = virtualMachine.getPrimaryNetworkInterface().id();
        String ipId = virtualMachine.getPrimaryPublicIPAddressId();
        return azureResourceManager.networkInterfaces()
                .getById(networkId)
                .update()
                .withoutPrimaryPublicIPAddress()
                .applyAsync().toFuture()
                .exceptionallyCompose(throwable -> {
                    log.error(throwable.getMessage());
                    throw new RuntimeException(throwable.getMessage());
                })
                .thenAccept(unused -> azureResourceManager.publicIpAddresses().deleteById(ipId));
    }

}
