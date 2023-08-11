package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.InstanceEntity;
import com.antithesis.cloudmag.model.Instance;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InstanceMapper {


    Instance mapToInstance(InstanceEntity instanceEntity);

    Set<Instance> mapToInstanceSet(Set<InstanceEntity> instanceEntitySet);
}
