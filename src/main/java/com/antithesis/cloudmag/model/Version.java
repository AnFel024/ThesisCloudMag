package com.antithesis.cloudmag.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class Version {
    private UUID id;
    private Long createdAt;
    private String name;
    private String description;
    private String branchName;
    private String tagName;
    private String status;
    private User creator;
    private Project projectInfo;
    private String date;
}

