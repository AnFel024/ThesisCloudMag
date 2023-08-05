package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.ProjectService;
import com.antithesis.cloudmag.service.ValidationsService;
import com.antithesis.cloudmag.service.VersionService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ValidationController {

    private final ValidationsService validationsService;
    private final ProjectService projectService;

    @GetMapping("/project_created/{projectName}")
    public ResponseEntity<?> refreshProject(@PathVariable String projectName) {
        MessageResponse<?> messageResponse = validationsService.validateProject(projectName);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/database_created/{databaseName}")
    public ResponseEntity<?> refreshDatabase(@PathVariable String databaseName) {
        MessageResponse<?> messageResponse = validationsService.validateDatabase(databaseName);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/version_created/{versionName}")
    public ResponseEntity<?> refreshVersion(@PathVariable String versionName) {
        MessageResponse<?> messageResponse = validationsService.validateVersion(versionName);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/deploy_created/{deployName}/tag/{tagName}")
    public ResponseEntity<?> refreshDeploy(@PathVariable String deployName, @PathVariable String tagName) {
        MessageResponse<?> messageResponse = validationsService.validateDeploy(deployName, tagName);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/validate-instances")
    public ResponseEntity<?> testInstance() {
        return ResponseEntity.ok().body(projectService.validateInstanceStatus());
    }

    @GetMapping("/validate-databases")
    public ResponseEntity<?> testDatabase() {
        return ResponseEntity.ok().body(projectService.validateDatabaseStatus());
    }
}
