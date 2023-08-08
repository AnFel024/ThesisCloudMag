package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.*;
import com.antithesis.cloudmag.service.ProjectService;
import com.antithesis.cloudmag.utils.ResponseUtils;
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

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProject(@Valid @RequestBody DeleteAppDto deleteAppDto) {
        return ResponseEntity.ok().body(projectService.deleteProject(deleteAppDto));
    }
}
