package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.DeployEntity;
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
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<?> createVersion(CreateVersionDto createVersionDto) {
        DeployEntity versionEntity = DeployEntity.builder()

                .build();
        deployRepository.save(versionEntity);
        return MessageResponse.builder()
                .message("Version creada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse listDeploys() {
        // TODO pasar long fecha a localdatetime
        return MessageResponse.builder().status(HttpStatus.OK).data(deployRepository.findAll()).build();
    }
}
