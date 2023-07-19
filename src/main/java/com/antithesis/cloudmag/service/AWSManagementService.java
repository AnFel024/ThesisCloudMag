package com.antithesis.cloudmag.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AWSManagementService {
    private final Ec2Client ec2Client;

    public AWSManagementService(@Qualifier("ec2-instances-configuration") Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public List<List<String>> generateInstance() {
        /*
        # AWS region id     -> us-east-1
        # Security group id ->collect sg-049be70bb05fbf786
        # AMI id            -> ami-0557a15b87f6559cf
        # Key Pair          -> THESIS_WORK_KEY_PAIR
         */
        RunInstancesRequest request = RunInstancesRequest.builder()
                .securityGroupIds("sg-049be70bb05fbf786")
                .keyName("THESIS_WORK_KEY_PAIR")
                .instanceType(InstanceType.T2_MICRO)
                .imageId("ami-0557a15b87f6559cf")
                .minCount(1)
                .maxCount(1)
                .build();
        RunInstancesResponse runInstancesResponse = ec2Client.runInstances(request);
        if (!runInstancesResponse.hasInstances()) {
            throw new RuntimeException("Fallo en la creacion de instancias");
        }
        return runInstancesResponse.instances().stream().map(instance -> instance.networkInterfaces().stream().map(net -> net.privateIpAddress()).collect(Collectors.toList())).collect(Collectors.toList());
    }
}
