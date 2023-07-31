package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.CreateDeployDto;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.DeploysService;
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
    public ResponseEntity<?> createDeploy(@Valid @RequestBody CreateDeployDto createDeployDto) {
        MessageResponse<?> messageResponse = deploysService.createDeploy(createDeployDto);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getDeploys() {
        return ResponseEntity.ok().body(deploysService.listDeploys());
    }
}
