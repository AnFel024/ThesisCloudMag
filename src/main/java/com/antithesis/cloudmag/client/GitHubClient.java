package com.antithesis.cloudmag.client;

import com.antithesis.cloudmag.client.request.GitHubRequest;
import com.antithesis.cloudmag.client.responses.GitHubResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GitHubClient {
    private static final String TARGET_URL = "https://api.github.com/orgs/cloudmag-tesis/repos";

    private final String GAT;
    private final ObjectMapper objectMapper;

    public GitHubClient(@Value("${github.gh-credentials}") String gat, ObjectMapper objectMapper) {
        GAT = gat;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public String createRepository(String repositoryName) {
        URI targetURI = new URI(TARGET_URL);
        GitHubRequest body = GitHubRequest.builder()
                .name(repositoryName)
                .build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .header("Authorization", "token " + GAT)
                .POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body)))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        GitHubResponse clientResponse = objectMapper.readValue(response.body(), GitHubResponse.class);
        return clientResponse.toString();
    }
}
