package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.request.JenkinsDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.DeployEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import com.antithesis.cloudmag.repository.DeployRepository;
import com.antithesis.cloudmag.repository.UserRepository;
import com.antithesis.cloudmag.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DeploysService {

    private final DeployRepository deployRepository;
    private final UserRepository userRepository;
    private final JenkinsClient jenkinsClient;
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<String> createVersion(CreateVersionDto createVersionDto) {
        UserEntity userEntity = userRepository.findById(
                createVersionDto.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                format("User with id %s not found", createVersionDto.getUsername())));
        Boolean success = jenkinsClient.triggerJob(createVersionDto.getAppOrg(),
                createVersionDto.getAppUrl(),
                createVersionDto.getAppName(),
                createVersionDto.getBranchName(),
                createVersionDto.getBranchType(),
                createVersionDto.getCreateVersion(),
                createVersionDto.getVersionType());
        DeployEntity versionEntity = DeployEntity.builder()
                .creator(userEntity)
                .status(success ? "SUCCESS" : "FAILED")
                .build();
        deployRepository.save(versionEntity);
        return MessageResponse.<String>builder()
                .data("Lanzar la URL del pipeline")
                .message("Version creada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<?> listDeploys(String repo_id) {
        // TODO pasar long fecha a localdatetime
        return MessageResponse.builder().status(HttpStatus.OK).data(
                deployRepository.findAllByProjectInfoName(repo_id)).build();
    }
}
