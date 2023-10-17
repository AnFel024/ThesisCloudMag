package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.service.AuditsService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/audits")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuditsController {

    private final AuditsService auditsService;

    @GetMapping("/projects")
    public ResponseEntity<?> audits() {
        return ResponseUtils.validateResponse(auditsService.audits());
    }
}
