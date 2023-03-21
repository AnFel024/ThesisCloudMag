package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.service.AWSManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private final AWSManagementService awsManagementService;

    public MainController(AWSManagementService awsManagementService) {
        this.awsManagementService = awsManagementService;
    }

    @GetMapping("/hey")
    public ResponseEntity<?> prueba() {
        return ResponseEntity.ok().body(awsManagementService.generateInstance());
    }
}
