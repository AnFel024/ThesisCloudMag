package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.entity.Project;
import com.antithesis.cloudmag.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class CreateProjectService {

    private final ProjectRepository projectRepository;

    public CreateProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void createProject(String name, String userOwner) {
        UUID uuid = UUID.randomUUID();
        Project project = Project.builder()
                .id(uuid)
                .project_name(name)
                .created_at(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .creator(userOwner)
                .repository_url("abc123")
                .build();
        // TODO Post body
        projectRepository.save(project);
    }
}
