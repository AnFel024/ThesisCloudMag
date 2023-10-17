package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.*;
import com.antithesis.cloudmag.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneId.SHORT_IDS;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ValidationsService {

    private final VersionRepository versionRepository;
    private final ProjectRepository projectRepository;
    private final StatusRepository statusRepository;
    private final DatabaseRepository databaseRepository;
    private final DeployRepository deployRepository;

    public MessageResponse<String> validateVersion(String versionId, String status) {
        VersionEntity versionEntity = versionRepository.findById(UUID.fromString(versionId)).orElseThrow();
        versionEntity.setStatus(status);
        versionRepository.save(versionEntity);
        return MessageResponse.<String>builder()
                .message("Version creada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<String> validateDatabase(String databaseName, String status) {
        DatabaseEntity databaseEntity = databaseRepository.findByName(databaseName);
        databaseEntity.setStatus(status);
        databaseRepository.save(databaseEntity);
        return MessageResponse.<String>builder()
                .message("Base de datos refrescada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<String> validateProject(String projectName, String status) {
        ProjectEntity projectEntity = projectRepository.findByName(projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        StatusEntity statusEntity = projectEntity.getStatus();
        statusEntity.setStatusName(status);
        statusEntity.setUpdatedAt(LocalDateTime.now(ZoneId.of("America/Bogota")).toInstant(ZoneOffset.UTC).toEpochMilli());
        statusRepository.save(statusEntity);
        projectEntity.setStatus(statusEntity);
        projectRepository.save(projectEntity);
        return MessageResponse.<String>builder()
                .message("Proyecto actualizado")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<String> validateDeploy(String projectName, String versionId, String status) {
        List<DeployEntity> deployEntities = deployRepository.findAllByProjectInfoName(projectName);
        if ("ACTIVE".equals(status)) {
            deployEntities.stream()
                    .peek(deployEntity -> deployEntity.setStatus("FINISHED"))
                    .forEach(deployRepository::save);
        }
        DeployEntity actual = deployEntities.stream()
                .filter(deployEntity -> deployEntity.getVersionInfo().getId().equals(UUID.fromString(versionId)))
                .findFirst()
                .orElseThrow();
        actual.setStatus(status);
        deployRepository.save(actual);
        return MessageResponse.<String>builder()
                .message("Despliegue refrescado")
                .status(HttpStatus.OK)
                .build();
    }
}
