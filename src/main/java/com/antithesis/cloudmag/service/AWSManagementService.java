package com.antithesis.cloudmag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.DateInterval;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Granularity;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.time.LocalDate;
import java.util.List;

import static software.amazon.awssdk.regions.Region.US_EAST_1;

@Service
@Slf4j
public class AWSManagementService {
    private final Ec2Client ec2Client;
    private final CostExplorerClient costExplorerClient;

    public AWSManagementService(@Qualifier("ec2-instances-configuration") AwsBasicCredentials awsBasicCredentials) {
        this.ec2Client = Ec2Client.builder()
                .region(US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
        this.costExplorerClient = CostExplorerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    public RunInstancesResponse generateInstance(InstanceType instanceType) {
        RunInstancesRequest request = RunInstancesRequest.builder()
                .securityGroupIds("sg-05f6e938375a4b58e")
                .keyName("THESIS-KEY-PAIR")
                .instanceType(instanceType)
                .imageId("ami-0557a15b87f6559cf")
                .minCount(1)
                .maxCount(1)
                .blockDeviceMappings(
                        BlockDeviceMapping.builder()
                                .deviceName("/dev/sda1")
                                .ebs(
                                        EbsBlockDevice.builder()
                                                .volumeSize(20)
                                                .deleteOnTermination(true)
                                                .volumeType(VolumeType.GP2)
                                                .build()
                                )
                                .build())
                .build();
        RunInstancesResponse runInstancesResponse = ec2Client.runInstances(request);
        if (!runInstancesResponse.hasInstances()) {
            throw new RuntimeException("Fallo en la creacion de instancias");
        }

        return runInstancesResponse;
    }

    public boolean terminateInstance(String instanceID) {
        try{
            TerminateInstancesRequest ti = TerminateInstancesRequest.builder()
                    .instanceIds(instanceID)
                    .build();
            TerminateInstancesResponse response = ec2Client.terminateInstances(ti);
            List<InstanceStateChange> list = response.terminatingInstances();
            for (InstanceStateChange sc : list) {
                log.info("The ID of the terminated instance is {}", sc.instanceId());
            }
            return ti.hasInstanceIds();
        } catch (Ec2Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
        return false;
    }

    public DescribeInstancesResponse validateInstanceHealth() {
        return ec2Client.describeInstances();
    }

    public GetCostAndUsageResponse describeCosts() {
        LocalDate localDate = LocalDate.now();
        String year = String.valueOf(localDate.getYear());
        int monthValue = localDate.getMonthValue();
        String month = monthValue < 10 ? "0" + monthValue : String.valueOf(monthValue);
        int dayValue = localDate.getDayOfMonth();
        String day = dayValue < 10 ? "0" + dayValue : String.valueOf(dayValue);
        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
                .timePeriod(DateInterval.builder()
                        .start(year + "-" + month + "-" + "01")
                        .end(year + "-" + month + "-" + day)
                        .build())
                .granularity(Granularity.DAILY)
                .metrics("BlendedCost")
                .build();

        return costExplorerClient.getCostAndUsage(request);

    }
}
