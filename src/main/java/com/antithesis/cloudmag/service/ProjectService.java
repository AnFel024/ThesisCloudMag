package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.entity.Project;
import com.antithesis.cloudmag.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProjectService {

    private final ProjectRepository projectRepository;

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

    public List<Project> listProjects(String userOwner) {
        projectRepository.findAll();
    }
}
