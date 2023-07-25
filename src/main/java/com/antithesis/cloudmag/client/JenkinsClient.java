package com.antithesis.cloudmag.client;

import com.antithesis.cloudmag.client.request.JenkinsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class JenkinsClient {
    private static final String BASE_URL = "http://192.168.5.111:8080";
    private static final String TARGET_URL = "%s/job/%s/buildWithParameters";
    private static final String TOKEN = "token";
    private static final String APP_NAME = "app_name";

    private final String token;
    private final ObjectMapper objectMapper;

    public JenkinsClient(@Value("${jenkins.credentials}") String token, ObjectMapper objectMapper) {
        this.token = token;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public Boolean triggerJob(String appOrg,
                              String appUrl,
                              String appName,
                              String branchName,
                              String branchType,
                              String createVersion,
                              String versionType) {
        URI targetURI = new URI(TARGET_URL);
        JenkinsRequest jenkinsRequest = JenkinsRequest.builder()
                .token(token)
                .appOrg(appOrg)
                .appUrl(appUrl)
                .appName(appName)
                .branchName(branchName)
                .branchType(branchType)
                .createVersion(createVersion)
                .versionType(versionType)
                .build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .header("Authorization", "Basic " + "") // TODO Validar crumb id
                .POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(jenkinsRequest)))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == HttpStatus.CREATED.value();
    }
}
