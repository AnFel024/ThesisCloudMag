package com.antithesis.cloudmag;

import com.antithesis.cloudmag.tools.ProfileTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.ConfigurableEnvironment;

@ConfigurationPropertiesScan("com.antithesis.cloudmag.configuration")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class CloudMagApplication {

    public static void main(String[] args) {
        ProfileTool.setLoaderProfile();
        SpringApplication.run(CloudMagApplication.class, args);
    }
}
