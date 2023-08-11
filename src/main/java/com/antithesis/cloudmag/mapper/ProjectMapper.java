package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.model.Project;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {
        InstanceMapper.class,
        UserMapper.class
})
public interface ProjectMapper {
    Project mapToProject(ProjectEntity projectEntity);

    Set<Project> mapToProjectSet(Set<ProjectEntity> projectEntitySet);
}
