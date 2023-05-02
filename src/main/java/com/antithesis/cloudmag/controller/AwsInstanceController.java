package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.service.AWSKeyPairService;
import com.antithesis.cloudmag.service.AWSManagementService;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
public class AwsInstanceController {
    private final AWSManagementService awsManagementService;
    private final AWSKeyPairService awsKeyPairService;

    private final GitHubClient gitHubClient;

    public AwsInstanceController(AWSManagementService awsManagementService, AWSKeyPairService awsKeyPairService, GitHubClient gitHubClient) {
        this.awsManagementService = awsManagementService;
        this.awsKeyPairService = awsKeyPairService;
        this.gitHubClient = gitHubClient;
    }

    @SneakyThrows
    @GetMapping("/create_keypair")
    public ResponseEntity<Resource> createKeyPair(@RequestParam String name) {
        InputStream createKeyPairResponse = awsKeyPairService.generateKeyPair(name);
        InputStreamResource resource = new InputStreamResource(createKeyPairResponse);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/create_instance")
    public ResponseEntity<?> createInstance() {
        awsManagementService.generateInstance();
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/create-reposiroty/{name}")
    public ResponseEntity<?> testRest(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(gitHubClient.createRepository(name));
    }

    @GetMapping("/hello_world")
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok().body("Hola mundo!");
    }
}
