package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {
        InstanceMapper.class,
        UserMapper.class
})
public interface ProjectMapper {
    @Mapping(target = "date", ignore = true)
    Project mapToProject(ProjectEntity projectEntity);

    Set<Project> mapToProjectSet(Set<ProjectEntity> projectEntitySet);
}
