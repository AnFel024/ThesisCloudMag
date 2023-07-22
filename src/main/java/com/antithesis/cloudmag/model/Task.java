package com.antithesis.cloudmag.model;

import com.antithesis.cloudmag.entity.UserEntity;
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
public class Task {
    private UUID id;
    private Long createdAt;
    private String name;
    private String concurrentUrl;
    private String scheduledTime;
    private User creator;
}
