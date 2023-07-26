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
@RequestMapping("/version")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class VersionController {

    private final VersionService versionService;
    private final GitHubClient gitHubClient;

    @PostMapping("/create")
    public ResponseEntity<?> createVersions(@Valid @RequestBody CreateVersionDto createVersionDto) {
        MessageResponse<?> messageResponse = versionService.createVersion(createVersionDto);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getVersions() {
        return ResponseEntity.ok().body(versionService.listVersions());
    }

    @GetMapping("/list/org/{org}/repo/{repo_id}/branches")
    public ResponseEntity<?> getBranches(@PathVariable String repo_id, @PathVariable String org) {
        return ResponseEntity.ok().body(gitHubClient.listBranches(org, repo_id));
    }
}
