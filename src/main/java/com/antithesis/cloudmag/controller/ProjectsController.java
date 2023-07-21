package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.CreateAppRequest;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseRequest;
import com.antithesis.cloudmag.controller.payload.request.CreateTaskRequest;
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

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getProjects(@PathVariable String user_id) {
        return ResponseUtils.validateResponse(projectService.listProjects(user_id));
    }

    @PostMapping("/create-project/user/{user_id}")
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateAppRequest createAppRequest, @PathVariable String user_id) {
        return ResponseUtils.validateResponse(projectService.createProject(createAppRequest, user_id));
    }

    @PostMapping("/create-database/user/{user_id}")
    public ResponseEntity<?> createDDBB(@Valid @RequestBody CreateDatabaseRequest createDatabaseRequest, @PathVariable String user_id) {
        return ResponseUtils.validateResponse(projectService.createDatabase(createDatabaseRequest, user_id));
    }

    @PostMapping("/create-task/user/{user_id}")
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest, @PathVariable String user_id) {
        return ResponseUtils.validateResponse(projectService.createTask(createTaskRequest, user_id));
    }

    @DeleteMapping("/user/{user_id}/name/{project_name}")
    public ResponseEntity<?> deleteProject(@PathVariable String user_id, @PathVariable String project_name) {
        return ResponseUtils.validateResponse(projectService.deleteProject(user_id, project_name));
    }

    @DeleteMapping("/task/user/{user_id}/name/{task_name}")
    public ResponseEntity<?> deleteTask(@PathVariable String user_id, @PathVariable String task_name) {
        return ResponseUtils.validateResponse(projectService.deleteTask(user_id, task_name));
    }

}
