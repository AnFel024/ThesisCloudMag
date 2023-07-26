package com.antithesis.cloudmag.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Component
public class JenkinsClient {
    private static final String BASE_URL = "http://52.44.232.147:8080";
    private static final String TARGET_URL = "%s/job/%s/buildWithParameters?%s";
    private final String token;

    public JenkinsClient(@Value("${jenkins.credentials}") String token) {
        this.token = token;
    }

    @SneakyThrows
    public Boolean triggerJob(String appOrg,
                              String appUrl,
                              String appName,
                              String branchName,
                              String versionTag,
                              String branchType) {
        String params = String.join("&", java.util.List.of("token=my_token","app_org=" + appOrg,
                "app_name="+ appName,
                "app_url=" + appUrl,
                "branch_name=" + branchName,
                "branch_type=" + branchType,
                "version_tag=" + versionTag));
        return triggerInJenkins(params, "createversion");
    }

    @SneakyThrows
    public Boolean triggerDeployJob(String dockerImageName, String dockerImageTag, String ipDir, String dockerContainerName) {
        String params = String.join("&", java.util.List.of("token=my_token",
                "docker_container_name=" + dockerContainerName,
                "docker_image_name=" + dockerImageName,
                "docker_image_tag=" + dockerImageTag,
                "ip_dir=" + ipDir));
        return triggerInJenkins(params, "deploy");
    }

    private Boolean triggerInJenkins(String params, String jobName) throws URISyntaxException, java.io.IOException, InterruptedException {
        final String targetUrl = String.format(TARGET_URL, BASE_URL, jobName, params);
        URI targetURI = new URI(targetUrl);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .header("Authorization", getBasicAuthenticationHeader("admin", token))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == HttpStatus.CREATED.value();
    }

    private String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
