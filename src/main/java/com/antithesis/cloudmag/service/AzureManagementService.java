package com.antithesis.cloudmag.service;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import com.azure.resourcemanager.costmanagement.CostManagementManager;
import com.azure.resourcemanager.costmanagement.models.*;
import com.azure.resourcemanager.network.models.NetworkSecurityGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AzureManagementService {

    private static final String REGION = "eastus";

    public static final String GROUP_NAME = "tesis-resource-groups";

    private final String sshPublicKey;
    private final AzureResourceManager azureResourceManager;
    private final CostManagementManager costManagementManager;

    public AzureManagementService(@Qualifier("azure-vms-configuration") AzureResourceManager azureResourceManager,
                                    @Qualifier("azure-cost-configuration") CostManagementManager costManagementManager,
                                  @Value("${ssh.public.key}") String sshPublicKey) {
        this.azureResourceManager = azureResourceManager;
        this.costManagementManager = costManagementManager;
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
                .withSsh("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCj+Yg9D5CGjiNPKMIDW9Wdvz+" +
                        "45RWLJ+" +
                        "Vlw4Ev6JROx209AUdtIYRVMfQdINy9l5fgGZLK7L7sOg1JnDFQGgr9kw7Ck3KRE3RfUvYZOC1" +
                        "cJ9xIo2JKrXJWmyebOnpIUzTCd45WweLT/5E/mvQ3syFOmrBuLAzZHYYW3tnkKhrGPQ" +
                        "== " +
                        "anfelpe.0200@gmail.com+")
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

    public List<BlobInfo> describeCosts() {
        GenerateCostDetailsReports generateCostDetailsReports = costManagementManager.generateCostDetailsReports();
        GenerateCostDetailsReportRequestDefinition requestDefinition = new GenerateCostDetailsReportRequestDefinition()
                    .withMetric(CostDetailsMetricType.ACTUAL_COST)
                    .withTimePeriod(new CostDetailsTimePeriod().withStart("2023-07-10").withEnd("2023-08-09"));
        return generateCostDetailsReports.createOperation(
                "subscriptions/" + azureResourceManager.subscriptionId(), requestDefinition).blobs();
    }

}
