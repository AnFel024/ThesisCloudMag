package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.AzureManagementService;
import com.antithesis.cloudmag.service.ProjectService;
import com.antithesis.cloudmag.service.ValidationsService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ValidationController {

    private final ValidationsService validationsService;
    private final AzureManagementService azureManagementService;
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

    @GetMapping("/version/{versionId}/status/{status}")
    public ResponseEntity<?> refreshVersion(@PathVariable String versionId, @PathVariable String status) {
        MessageResponse<?> messageResponse = validationsService.validateVersion(versionId, status);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/deploy_created/{deployName}/tag/{versionId}")
    public ResponseEntity<?> refreshDeploy(@PathVariable String deployName, @PathVariable String versionId) {
        MessageResponse<?> messageResponse = validationsService.validateDeploy(deployName, versionId);
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
