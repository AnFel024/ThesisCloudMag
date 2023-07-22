package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.DatabaseEntity;
import com.antithesis.cloudmag.model.Database;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        InstanceMapper.class,
        UserMapper.class
})
public interface DatabaseMapper {

    Database mapToDatabase(DatabaseEntity databaseEntity);

    Set<Database> mapToDatabaseSet(Set<DatabaseEntity> databaseEntitySet);
}
