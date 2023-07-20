package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.service.ProjectService;
import com.antithesis.cloudmag.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/list")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ListsController {

    private final ProjectService projectService;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/projects/user/{user_id}")
    public ResponseEntity<?> getProjects(@PathVariable String user_id) {
        return ResponseEntity.ok().body(projectService.listProjects(user_id));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok().body(userDetailsService.getAllUsers());
    }
}
