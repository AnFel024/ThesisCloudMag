package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.DeployEntity;
import com.antithesis.cloudmag.model.Deploy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {InstanceMapper.class, VersionMapper.class, UserMapper.class})
public interface DeployMapper {

    Deploy mapToDeploy(DeployEntity deployEntity);

    Set<Deploy> mapToDeploySet(Set<DeployEntity> deployEntitySet);
}
