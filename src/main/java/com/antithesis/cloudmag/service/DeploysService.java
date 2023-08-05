package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.controller.payload.request.CreateDeployDto;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.*;
import com.antithesis.cloudmag.mapper.DeployMapper;
import com.antithesis.cloudmag.model.Deploy;
import com.antithesis.cloudmag.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DeploysService {

    private final ProjectRepository projectRepository;
    private final VersionRepository versionRepository;
    private final DeployMapper deployMapper;
    private final DeployRepository deployRepository;
    private final UserRepository userRepository;
    private final JenkinsClient jenkinsClient;
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<String> createDeploy(CreateDeployDto createDeployDto) {
        UserEntity userEntity = userRepository.findById(
                createDeployDto.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                format("User with id %s not found", createDeployDto.getUsername())));
        VersionEntity versionEntity = versionRepository.findByTagName(createDeployDto.getTag());
        Boolean success = jenkinsClient.triggerDeployJob(
                createDeployDto.getAppName(),
                createDeployDto.getTag(),
                createDeployDto.getIpDir());
        DeployEntity deployEntity = DeployEntity.builder()
                .creator(userEntity)
                .instanceInfo(versionEntity.getProjectInfo().getInstanceInfo())
                .versionInfo(versionEntity)
                .status(success ? "SUCCESS" : "FAILED")
                .build();
        deployRepository.save(deployEntity);
        return MessageResponse.<String>builder()
                .message("Despliegue creado")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<List<Deploy>> listDeploys() {
        return MessageResponse.<List<Deploy>>builder().status(HttpStatus.OK).data(
                deployRepository.findAll().stream().map(deployMapper::mapToDeploy).toList()).build();
    }
}
