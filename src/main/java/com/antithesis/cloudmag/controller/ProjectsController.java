package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.CreateAppDto;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseDto;
import com.antithesis.cloudmag.controller.payload.request.CreateTaskDto;
import com.antithesis.cloudmag.controller.payload.request.ListInfoDto;
import com.antithesis.cloudmag.service.ProjectService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import com.azure.core.annotation.Get;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/project")

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProjectsController {

    private final ProjectService projectService;

    @PostMapping("/list-by-user")
    public ResponseEntity<?> getProjects() {
        return ResponseUtils.validateResponse(projectService.listProjects());
    }

    @PostMapping("/list-databases") // TODO pending
    public ResponseEntity<?> getDatabases() {
        return ResponseUtils.validateResponse(projectService.listDatabases());
    }

    @PostMapping("/create-project")
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateAppDto createAppDto) {
        return ResponseUtils.validateResponse(projectService.createProject(createAppDto));
    }

    @PostMapping("/create-database")
    public ResponseEntity<?> createDDBB(@Valid @RequestBody CreateDatabaseDto createDatabaseDto) {
        return ResponseUtils.validateResponse(projectService.createDatabase(createDatabaseDto));
    }

    @GetMapping("/validate-instances")
    public ResponseEntity<?> testInstance() {
        return ResponseEntity.ok().body(projectService.validateInstanceStatus());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteProject(@PathVariable String name) {
        return ResponseEntity.ok().body(projectService.deleteProject(name));
    }
}
