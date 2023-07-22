package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.Project;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.UUID;

import static org.mapstruct.factory.Mappers.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    Project mapToProject(ProjectEntity projectEntity);
}
