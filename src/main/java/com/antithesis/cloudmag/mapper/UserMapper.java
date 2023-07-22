package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.InstanceEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.Instance;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserMapper.class);

    User mapToUser(UserEntity userEntity);
}
