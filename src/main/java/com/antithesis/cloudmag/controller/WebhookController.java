package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.VersionService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WebhookController {

    private final VersionService versionService;
    private final GitHubClient gitHubClient;

    @PostMapping("/github/webhook")
    public ResponseEntity<?> createVersions(@Valid @RequestBody CreateVersionDto createVersionDto) {
        MessageResponse<?> messageResponse = versionService.createVersion(createVersionDto);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @PostMapping("/jenkins/events")
    public ResponseEntity<?> finishVersion(@Valid @RequestBody CreateVersionDto createVersionDto) {
        MessageResponse<?> messageResponse = versionService.createVersion(createVersionDto);
        return ResponseUtils.validateResponse(messageResponse);
    }
}
