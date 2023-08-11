package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.AzureManagementService;
import com.antithesis.cloudmag.service.ProjectService;
import com.antithesis.cloudmag.service.ValidationsService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ValidationController {

    private final ValidationsService validationsService;
    private final AzureManagementService azureManagementService;
    private final ProjectService projectService;

    @GetMapping("/project/{projectName}/status/{status}")
    public ResponseEntity<?> refreshProject(@PathVariable String projectName, @PathVariable String status) {
        MessageResponse<?> messageResponse = validationsService.validateProject(projectName, status);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/database/{databaseName}/status/{status}")
    public ResponseEntity<?> refreshDatabase(@PathVariable String databaseName, @PathVariable String status) {
        MessageResponse<?> messageResponse = validationsService.validateDatabase(databaseName, status);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/version/{versionId}/status/{status}")
    public ResponseEntity<?> refreshVersion(@PathVariable String versionId, @PathVariable String status) {
        MessageResponse<?> messageResponse = validationsService.validateVersion(versionId, status);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/deploy/{deployName}/tag/{versionId}/status/{status}")
    public ResponseEntity<?> refreshDeploy(@PathVariable String deployName, @PathVariable String versionId, @PathVariable String status) {
        MessageResponse<?> messageResponse = validationsService.validateDeploy(deployName, versionId, status);
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
