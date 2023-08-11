package com.antithesis.cloudmag.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String language;
}

