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

import static java.lang.String.format;

@Component
public class GitHubClient {
    private static final String BASE_URL = "https://api.github.com";
    private static final String CREATE_REPO_TARGET_URL = "%s/repos/cloudmag-tesis/spring-template/generate";
    private static final String LIST_BRANCH_TARGET_URL = "%s/repos/%s/%s/branches";

    /*
    curl --location 'https://api.github.com/repos/cloudmag-tesis/spring-template/generate' \
        --header 'Accept: application/vnd.github+json' \
        --header 'Authorization: Bearer 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' \
        --header 'Content-Type: application/json' \
        --header 'Cookie: _octo=GH1.1.1705964187.1680918574; logged_in=no' \
        --data '{
            "owner":"cloudmag-tesis",
            "name":"template-generated",
            "description":"Repo created from a template",
            "include_all_branches":false,
            "private":true
        }'
     */
    private final String GAT;
    private final ObjectMapper objectMapper;

    public GitHubClient(@Value("${github.gh-credentials}") String gat, ObjectMapper objectMapper) {
        this.GAT = gat;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public GitHubCreateRepositoryResponse createRepository(String repositoryName) {
        URI targetURI = new URI(format(CREATE_REPO_TARGET_URL, BASE_URL));
        String body = String.join("&", List.of(
                "name="+repositoryName,
                "owner="+"cloudmag-tesis",
                "description="+"Generated template",
                "include_all_branches="+"false",
                "private="+"true"
        ));
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
}
