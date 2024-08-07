package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.controller.payload.request.CreateDeployDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.*;
import com.antithesis.cloudmag.mapper.DeployMapper;
import com.antithesis.cloudmag.model.Deploy;
import com.antithesis.cloudmag.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        VersionEntity versionEntity = versionRepository.findById(UUID.fromString(createDeployDto.getTagId())).orElseThrow();
        Boolean success = jenkinsClient.triggerDeployJob(
                versionEntity.getId().toString(),
                createDeployDto.getAppName(),
                versionEntity.getTagName(),
                createDeployDto.getIpDir(),
                "AWS".equals(versionEntity.getProjectInfo().getInstanceInfo().getProvider()) ? "key.pem" : "id_rsa");
        DeployEntity deployEntity = DeployEntity.builder()
                .creator(userEntity)
                .instanceInfo(versionEntity.getProjectInfo().getInstanceInfo())
                .versionInfo(versionEntity)
                .createdAt(LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC).toEpochMilli())
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
                deployRepository.findAll().stream()
                        .map(deployEntity -> {
                            Deploy deploy = deployMapper.mapToDeploy(deployEntity);
                            deploy.setDate(LocalDateTime.ofInstant(
                                    java.time.Instant.ofEpochMilli(deployEntity.getCreatedAt()),
                                    java.time.ZoneId.systemDefault()).toString());
                            return deploy;
                        })
                        .toList()).build();
    }
}
