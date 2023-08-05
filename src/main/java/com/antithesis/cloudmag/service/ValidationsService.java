package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.controller.payload.request.CreateVersionDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.DatabaseEntity;
import com.antithesis.cloudmag.entity.DeployEntity;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import com.antithesis.cloudmag.mapper.VersionMapper;
import com.antithesis.cloudmag.model.Version;
import com.antithesis.cloudmag.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ValidationsService {

    private final VersionRepository versionRepository;
    private final ProjectRepository projectRepository;
    private final DatabaseRepository databaseRepository;
    private final DeployRepository deployRepository;

    public MessageResponse<String> validateVersion(String versionName) {
        VersionEntity versionEntity = versionRepository.findByTagName(versionName);
        versionEntity.setStatus("CREATED");
        versionRepository.save(versionEntity);
        return MessageResponse.<String>builder()
                .message("Version creada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<String> validateDatabase(String databaseName) {
        DatabaseEntity databaseEntity = databaseRepository.findByName(databaseName);
        databaseEntity.setStatus("CREATED");
        databaseRepository.save(databaseEntity);
        return MessageResponse.<String>builder()
                .message("Base de datos creada")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<String> validateProject(String projectName) {
        ProjectEntity projectEntity = projectRepository.findByName(projectName).orElseThrow();
        projectEntity.setStatus("CREATED");
        projectRepository.save(projectEntity);
        return MessageResponse.<String>builder()
                .message("Proyecto creado")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<String> validateDeploy(String projectName, String tagName) {
        List<DeployEntity> deployEntities = deployRepository.findAllByProjectInfoName(projectName);
        deployEntities.stream()
                .peek(deployEntity -> deployEntity.setStatus("FINISHED"))
                .forEach(deployRepository::save);
        DeployEntity actual = deployEntities.stream()
                .filter(deployEntity -> deployEntity.getVersionInfo().getTagName().equals(tagName))
                .findFirst()
                .orElseThrow();
        actual.setStatus("ACTIVE");
        deployRepository.save(actual);
        return MessageResponse.<String>builder()
                .message("Despliegue creado")
                .status(HttpStatus.CREATED)
                .build();
    }
}
