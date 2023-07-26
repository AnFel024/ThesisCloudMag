package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.service.AWSKeyPairService;
import com.antithesis.cloudmag.service.AWSManagementService;
import com.antithesis.cloudmag.service.AzureManagementService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.InputStream;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AwsInstanceController {
    private final AWSManagementService awsManagementService;
    private final AWSKeyPairService awsKeyPairService;
    private final AzureManagementService azureManagementService;
    private final GitHubClient gitHubClient;

    @SneakyThrows
    @GetMapping("/create_keypair")
    public ResponseEntity<Resource> createKeyPair(@RequestParam String name) {
        InputStream createKeyPairResponse = awsKeyPairService.generateKeyPair(name);
        InputStreamResource resource = new InputStreamResource(createKeyPairResponse);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

//    @GetMapping("/create_instance")
//    public ResponseEntity<?> createInstance() {
//        awsManagementService.generateInstance();
//        return ResponseEntity.ok().body("");
//    }

    @GetMapping("/validate-instances/status")
    public ResponseEntity<?> testRestStatus() {
        return ResponseEntity.ok().body(awsManagementService.validateInstanceHealth());
    }

//    @GetMapping("/create-azure")
//    public ResponseEntity<?> testAzure() {
//        return ResponseEntity.ok().body(azureManagementService.createVirtualMachine());
//    }

//    @GetMapping("/create-project/{name}/user/{user_id}")
//    public ResponseEntity<?> testRest(@PathVariable("name") String name, @PathVariable("user_id") String user_id) {
//        createProjectService.createProject(name, user_id);
//        return ResponseEntity.ok().body("Project created!");
//    }

    @GetMapping("/hello_world")
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok().body("Hola mundo!");
    }
}
