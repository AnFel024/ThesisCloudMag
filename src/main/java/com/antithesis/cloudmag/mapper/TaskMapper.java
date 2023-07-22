package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.TaskEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.model.Task;
import com.antithesis.cloudmag.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        UserMapper.class
})
public interface TaskMapper {

    Task mapToTask(TaskEntity taskEntity);

    Set<Task> mapToTaskSet(Set<TaskEntity> taskEntitySet);
}
