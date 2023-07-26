package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.DeployEntity;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.repository.DeployRepository;
import com.antithesis.cloudmag.repository.ProjectRepository;
import com.antithesis.cloudmag.repository.UserRepository;
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
    private final DeployRepository deployRepository;
    private final UserRepository userRepository;
    private final JenkinsClient jenkinsClient;
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<String> createDeploy(CreateVersionDto createVersionDto) {
        UserEntity userEntity = userRepository.findById(
                createVersionDto.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                format("User with id %s not found", createVersionDto.getUsername())));
        ProjectEntity projectEntities = projectRepository.findAll().stream().filter(
                projectEntity -> projectEntity.getName().equals(createVersionDto.getProjectName())
        ).findFirst().get();
        Boolean success = jenkinsClient.triggerDeployJob(
                projectEntities.getName() + "-" + createVersionDto.getTag(),
                projectEntities.getName(),
                createVersionDto.getTag(),
                projectEntities.getInstanceInfo().getHostUrl());
        DeployEntity versionEntity = DeployEntity.builder()
                .creator(userEntity)
                .status(success ? "SUCCESS" : "FAILED")
                .build();
        deployRepository.save(versionEntity);
        return MessageResponse.<String>builder()
                .data("Despliegue creado")
                .message("Version creada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<?> listDeploys() {
        return MessageResponse.builder().status(HttpStatus.OK).data(
                deployRepository.findAll()).build();
    }
}
