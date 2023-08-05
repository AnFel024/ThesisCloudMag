package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import com.antithesis.cloudmag.mapper.VersionMapper;
import com.antithesis.cloudmag.model.Version;
import com.antithesis.cloudmag.repository.ProjectRepository;
import com.antithesis.cloudmag.repository.UserRepository;
import com.antithesis.cloudmag.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class VersionService {

    private final VersionRepository versionRepository;
    private final ProjectRepository projectRepository;
    private final JenkinsClient jenkinsClient;
    private final UserRepository userRepository;
    private final VersionMapper versionMapper;
    private final DogStatsdClient dogStatsdClient;


    public MessageResponse<?> createVersion(CreateVersionDto createVersionDto) {
        String branchName = createVersionDto.getBranchName();
        if (!"master".equals(createVersionDto.getBranchName())
                && !"develop".equals(createVersionDto.getBranchType())
                && !"main".equals(createVersionDto.getBranchType())) {
            branchName = createVersionDto.getBranchType() + "/" + createVersionDto.getBranchName();
        }
        Boolean versionTriggered = jenkinsClient.triggerJob(
                createVersionDto.getAppUrl(),
                createVersionDto.getAppName(),
                branchName,
                createVersionDto.getTag()
        );
        ProjectEntity byName = projectRepository.findByName(createVersionDto.getAppName()).orElseThrow();
        VersionEntity versionEntity = VersionEntity.builder()
                .name(createVersionDto.getTag())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .tagName(createVersionDto.getTag())
                .creator(userRepository.findById(createVersionDto.getUsername()).get())
                .status(versionTriggered ? "PENDING" : "FAILED")
                .projectInfo(byName)
                .build();
        versionRepository.save(versionEntity);
        return MessageResponse.builder()
                .message(format(
                        "Version creada: %s", versionEntity.getName()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<List<Version>> listVersions() {
        // TODO pasar long fecha a localdatetime
        return MessageResponse.<List<Version>>builder()
                .status(HttpStatus.OK)
                .data(versionRepository.findAll().stream()
                        .map(versionMapper::mapToVersion)
                        .toList())
                .build();
    }
}
