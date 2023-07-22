package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.DeployEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import com.antithesis.cloudmag.model.Deploy;
import com.antithesis.cloudmag.model.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, UserMapper.class})
public interface VersionMapper {

    Version mapToVersion(VersionEntity versionEntity);

    Set<Version> mapToVersionSet(Set<VersionEntity> versionEntitySet);
}