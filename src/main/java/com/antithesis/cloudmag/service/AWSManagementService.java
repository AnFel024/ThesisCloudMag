package com.antithesis.cloudmag.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

@Service
public class AWSManagementService {
    private final Ec2Client ec2Client;

    public AWSManagementService(@Qualifier("ec2-instances-configuration") Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public RunInstancesResponse generateInstance(InstanceType instanceType) {
        /*
        # AWS region id     -> us-east-1
        # Security group id -> collect sg-049be70bb05fbf786
        # AMI id            -> ami-0557a15b87f6559cf
        # Key Pair          -> THESIS_WORK_KEY_PAIR
         */
        // TODO validar image id
        RunInstancesRequest request = RunInstancesRequest.builder()
                .securityGroupIds("sg-05f6e938375a4b58e")
                .keyName("THESIS-KEY-PAIR")
                .instanceType(instanceType)
                .imageId("ami-0557a15b87f6559cf")
                .minCount(1)
                .maxCount(1)
                .build();
        RunInstancesResponse runInstancesResponse = ec2Client.runInstances(request);
        if (!runInstancesResponse.hasInstances()) {
            throw new RuntimeException("Fallo en la creacion de instancias");
        }

        return runInstancesResponse;
    }

    public DescribeInstancesResponse validateInstanceHealth() {
        return ec2Client.describeInstances();
    }
}
