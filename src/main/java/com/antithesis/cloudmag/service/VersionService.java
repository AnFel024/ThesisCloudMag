package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
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
public class VersionService {

    private final VersionRepository versionRepository;
    private final UserRepository userRepository;
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<?> createVersion(CreateVersionDto createVersionDto) {
        UserEntity userEntity = userRepository.findById(createVersionDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
        VersionEntity versionEntity = VersionEntity.builder()
                .name(createVersionDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .creator(userEntity)
                .description(createVersionDto.getDescription())
                .tagName(createVersionDto.getTag())
                .build();
        versionRepository.save(versionEntity);
        return MessageResponse.builder()
                .message(format(
                        "Version creada: %s", versionEntity.getName()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse listVersions(String userOwner) {
        // TODO pasar long fecha a localdatetime
        return MessageResponse.builder().status(HttpStatus.OK).data(versionRepository.findAll()).build();
    }
}
