package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.InstanceEntity;
import com.antithesis.cloudmag.model.Instance;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InstanceMapper {
//    InstanceMapper INSTANCE = getMapper(InstanceMapper.class);


    Instance mapToInstance(InstanceEntity instanceEntity);
}
