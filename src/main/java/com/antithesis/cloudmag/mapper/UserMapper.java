package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {
                RoleMapper.class
})
public interface UserMapper {

    User mapToUser(UserEntity userEntity);

}
