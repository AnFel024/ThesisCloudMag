package com.antithesis.cloudmag.client;

import com.antithesis.cloudmag.client.responses.GitHubCreateRepositoryResponse;
import com.antithesis.cloudmag.client.responses.GitHubListBranchesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
public class GitHubClient {
    private static final String BASE_URL = "https://api.github.com";
    private static final String CREATE_REPO_TARGET_URL = "%s/repos/cloudmag-tesis/%s/generate";
    private static final String DELETE_REPO_TARGET_URL = "%s/repos/cloudmag-tesis/%s";
    private static final String LIST_BRANCH_TARGET_URL = "%s/repos/%s/%s/branches";
    private static final String JAVA_TEMPLATE_ID = "spring-template";
    private static final String PYTHON_TEMPLATE_ID = "django-template";

    private final String GAT;
    private final ObjectMapper objectMapper;

    public GitHubClient(@Value("${github.gh-credentials}") String gat, ObjectMapper objectMapper) {
        this.GAT = gat;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public GitHubCreateRepositoryResponse createRepository(String repositoryName, String language) {
        URI targetURI = new URI(format(
                CREATE_REPO_TARGET_URL,
                BASE_URL,
                "java".equals(language) ? JAVA_TEMPLATE_ID : PYTHON_TEMPLATE_ID));
        Map<String, Object > body = Map.of(
                "name",repositoryName,
                "owner","cloudmag-tesis",
                "description","Generated template",
                "include_all_branches",false,
                "private",true
        );
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .header("Authorization", "token " + GAT)
                .POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body)))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), GitHubCreateRepositoryResponse.class);
    }

    @SneakyThrows
    public List<GitHubListBranchesResponse> listBranches(String appOrg, String appName) {
        URI targetURI = new URI(format(LIST_BRANCH_TARGET_URL, BASE_URL, appOrg, appName));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .header("Authorization", "token " + GAT)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return Arrays.asList(objectMapper.readValue(response.body(), GitHubListBranchesResponse[].class));
    }

    @SneakyThrows
    public boolean deleteRepository(String appName) {
        URI targetURI = new URI(format(DELETE_REPO_TARGET_URL, BASE_URL, appName));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .header("Authorization", "token " + GAT)
                .DELETE()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 204;
    }
}
