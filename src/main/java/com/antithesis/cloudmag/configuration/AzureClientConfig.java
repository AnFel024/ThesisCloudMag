package com.antithesis.cloudmag.configuration;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Configuration
public class AzureClientConfig {

    @Bean
    @Primary
    @Qualifier("azure-vms-configuration")
    public static AzureResourceManager setAzureConfiguration() {

        TokenCredential credential = new EnvironmentCredentialBuilder()
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();

        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        return AzureResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withDefaultSubscription();
    }
}
