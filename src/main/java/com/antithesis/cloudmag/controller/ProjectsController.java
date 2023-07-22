package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.CreateAppDto;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseDto;
import com.antithesis.cloudmag.controller.payload.request.CreateTaskDto;
import com.antithesis.cloudmag.controller.payload.request.ListInfoDto;
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
    public ResponseEntity<?> getProjects(@Valid @RequestBody ListInfoDto listInfoDto) {
        return ResponseUtils.validateResponse(projectService.listProjects(listInfoDto.getUsername()));
    }

    @PostMapping("/list-task")
    public ResponseEntity<?> getDatabases() {
        return ResponseUtils.validateResponse(projectService.listDatabases());
    }

    @PostMapping("/list-databases")
    public ResponseEntity<?> getTasks() {
        return ResponseUtils.validateResponse(projectService.listTasks());
    }

    @PostMapping("/create-project")
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateAppDto createAppDto) {
        return ResponseUtils.validateResponse(projectService.createProject(createAppDto));
    }

    @PostMapping("/create-database/user/{user_id}")
    public ResponseEntity<?> createDDBB(@Valid @RequestBody CreateDatabaseDto createDatabaseDto, @PathVariable String user_id) {
        return ResponseUtils.validateResponse(projectService.createDatabase(createDatabaseDto, user_id));
    }

    @PostMapping("/create-task/user/{user_id}")
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskDto createTaskDto, @PathVariable String user_id) {
        return ResponseUtils.validateResponse(projectService.createTask(createTaskDto, user_id));
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
