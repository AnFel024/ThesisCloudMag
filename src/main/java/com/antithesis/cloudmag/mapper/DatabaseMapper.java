package com.antithesis.cloudmag.mapper;

import com.antithesis.cloudmag.entity.DatabaseEntity;
import com.antithesis.cloudmag.model.Database;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DatabaseMapper {

    Database mapToDatabase(DatabaseEntity databaseEntity);
}
