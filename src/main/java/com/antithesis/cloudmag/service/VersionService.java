package com.antithesis.cloudmag.service;

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
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class VersionService {

    private final VersionRepository versionRepository;
    private final ProjectRepository projectRepository;
    private final JenkinsClient jenkinsClient;
    private final UserRepository userRepository;
    private final VersionMapper versionMapper;


    public MessageResponse<?> createVersion(CreateVersionDto createVersionDto) {
        String[] split = createVersionDto.getBranchName().split("/");
        String branchName = split.length == 1 ? split[0] : split[0] + "/" + split[1];

        UUID uuid = UUID.randomUUID();
        ProjectEntity byName = projectRepository.findByName(createVersionDto.getAppName())
                .orElseThrow(() -> new RuntimeException("Project not found"));;
        Boolean versionTriggered = jenkinsClient.triggerVersionJob(
                createVersionDto.getAppUrl(),
                createVersionDto.getAppName(),
                branchName,
                createVersionDto.getTag(),
                uuid.toString(),
                byName.getLanguage()
        );
        VersionEntity versionEntity = VersionEntity.builder()
                .name(createVersionDto.getTag())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .tagName(createVersionDto.getTag())
                .branchName(branchName)
                .creator(userRepository.findById(createVersionDto.getUsername()).get())
                .status(versionTriggered ? "PENDING" : "FAILED")
                .projectInfo(byName)
                .id(uuid)
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
                        .map(versionEntity -> {
                            Version version = versionMapper.mapToVersion(versionEntity);
                            version.setDate(LocalDateTime.ofInstant(
                                    java.time.Instant.ofEpochMilli(versionEntity.getCreatedAt()),
                                    java.time.ZoneId.systemDefault()).toString());
                            return version;
                        })
                        .toList())
                .build();
    }
}
