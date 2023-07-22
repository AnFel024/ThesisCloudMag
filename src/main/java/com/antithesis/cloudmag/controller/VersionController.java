package com.antithesis.cloudmag.controller;

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

    @PostMapping("/create")
    public ResponseEntity<?> createVersions(@Valid @RequestBody CreateVersionDto createVersionDto) {
        MessageResponse<?> messageResponse = versionService.createVersion(createVersionDto);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/list/user/{user_id}/tags")
    public ResponseEntity<?> getVersions(@PathVariable String user_id) {
        return ResponseEntity.ok().body(versionService.listVersions(user_id));
    }
}
