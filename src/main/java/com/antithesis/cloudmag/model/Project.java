package com.antithesis.cloudmag.model;

import com.antithesis.cloudmag.entity.InstanceEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Project {
    private UUID id;
    private String name;
    private String repositoryUrl;
    private Long createdAt;
    private Status status;
    private Instance instanceInfo;
    private User creator;
    private String date;
    private String projectType;
}

