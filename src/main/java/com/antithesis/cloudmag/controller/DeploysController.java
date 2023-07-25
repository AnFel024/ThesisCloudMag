package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.DeploysService;
import com.antithesis.cloudmag.service.VersionService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/deploys")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DeploysController {

    private final DeploysService deploysService;

    @PostMapping("/create")
    public ResponseEntity<?> createDeploy(@Valid @RequestBody CreateVersionDto createVersionDto) {
        MessageResponse<?> messageResponse = deploysService.createVersion(createVersionDto);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/list/")
    public ResponseEntity<?> getDeploys(@PathVariable String repo_id) {
        return ResponseEntity.ok().body(deploysService.listDeploys(repo_id));
    }
}
