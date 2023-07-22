package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.InstanceEntity;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.Instance;
import com.antithesis.cloudmag.model.Project;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
//    ProjectMapper INSTANCE = getMapper(ProjectMapper.class);

//    @Mapping(source = "instanceInfo", target = "instanceInfo", qualifiedByName = "toSetInstance")
//    @Mapping(source = "creator", target = "creator", qualifiedByName = "toSetCreator")
    Project mapToProject(ProjectEntity projectEntity);
//
//    @Named("toSetInstance")
//    default Set<Instance> toSetInstance(Set<InstanceEntity> instanceMappers) {
//        return instanceMappers.stream()
//                .map(InstanceMapper.INSTANCE::mapToInstance)
//                .collect(Collectors.toSet());
//    }
//
//    @Named("toSetCreator")
//    default Set<User> toSetCreator(Set<UserEntity> userEntities) {
//        return userEntities.stream()
//                .map(UserMapper.INSTANCE::mapToUser)
//                .collect(Collectors.toSet());
//    }
}
