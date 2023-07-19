package com.antithesis.cloudmag.service;

import com.google.common.io.CharSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
@Slf4j
public class AWSKeyPairService {
    private final Ec2Client ec2Client;

    public AWSKeyPairService(@Qualifier("ec2-instances-configuration") Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public InputStream generateKeyPair(String name) {
        /*
        # AWS region id     -> us-east-1
        # Security group id ->collect sg-049be70bb05fbf786
        # AMI id            -> ami-0557a15b87f6559cf
        # Key Pair          -> THESIS_WORK_KEY_PAIR
         */
        CreateKeyPairRequest createKeyPairRequest = CreateKeyPairRequest.builder()
                .keyName(name)
                .keyFormat(KeyFormat.PEM)
                .keyType(KeyType.RSA)
                .build();
        CreateKeyPairResponse keyPair = ec2Client.createKeyPair(createKeyPairRequest);
        try {
            return CharSource.wrap(keyPair.keyMaterial()).asByteSource(StandardCharsets.UTF_8).openStream();
        } catch (IOException e) {
            log.error("Error -> ", e);
            ec2Client.deleteKeyPair(DeleteKeyPairRequest.builder().keyName(name).build());
            throw new RuntimeException("Fail to create response file", e);
        }
    }
}
