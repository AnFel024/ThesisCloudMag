package com.antithesis.cloudmag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;

@Service
@Slf4j
public class AWSManagementService {
    private final Ec2Client ec2Client;

    public AWSManagementService(@Qualifier("ec2-instances-configuration") Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
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
}
