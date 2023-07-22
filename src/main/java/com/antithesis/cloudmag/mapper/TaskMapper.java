package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.TaskEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.Task;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    Task mapToTask(TaskEntity userEntity);
}
