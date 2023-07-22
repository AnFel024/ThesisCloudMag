package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.RoleEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.Role;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role mapToUser(RoleEntity roleEntity);
}
