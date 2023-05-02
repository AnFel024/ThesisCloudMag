package com.antithesis.cloudmag.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Configuration
public class AwsClientConfig {

    public AwsClientConfig() {
    }

    @Bean
    @Primary
    @Qualifier("ec2-instances-configuration")
    public static Ec2Client setAwsConfiguration(@Value("${aws-credentials.aws-public-key}") String awsPublicKey, @Value("${aws-credentials.aws-private-key}") String awsPrivateKey) {
        Region region = Region.US_EAST_1;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsPublicKey, awsPrivateKey);
        return Ec2Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
