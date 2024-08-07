package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.StatusEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import com.antithesis.cloudmag.model.Status;
import com.antithesis.cloudmag.model.Version;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StatusMapper {

    Status mapToStatus(StatusEntity statusEntity);

    Set<Status> mapToStatusSet(Set<StatusEntity> statusEntitySet);
}
