package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = getMapper(UserMapper.class);

    User mapToUser(UserEntity userEntity);
}
