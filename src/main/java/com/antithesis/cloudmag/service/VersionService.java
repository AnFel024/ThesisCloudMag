package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionRequest;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.Version;
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
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<?> createVersion(CreateVersionRequest createVersionRequest) {
        Version version = Version.builder()
                .name(createVersionRequest.getName())
                .created_at(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .creator(createVersionRequest.getUsername())
                .description(createVersionRequest.getDescription())
                .tag_name(createVersionRequest.getTag())
                .build();
        versionRepository.save(version);
        return MessageResponse.builder()
                .message(format(
                        "Version creada: %s", version.getName()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse listVersions(String userOwner) {
        // TODO pasar long fecha a localdatetime
        return MessageResponse.builder().status(HttpStatus.OK).data(versionRepository.findAll()).build();
    }
}
