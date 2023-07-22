package com.antithesis.cloudmag.model;

import com.antithesis.cloudmag.entity.InstanceEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Database {
    private UUID databaseId;
    private String name;
    private String dbms;
    private User creator;
    private Long createdAt;
    private Instance instanceInfo;
}

