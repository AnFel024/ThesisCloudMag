package com.antithesis.cloudmag.entity;

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
@Entity
@Table(	name = "tasks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "taskId"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID taskId;

    private Long createdAt;

    @NotBlank
    private String name;

    @NotBlank
    private String concurrentUrl;

    @NotBlank
    private String scheduledTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "task_user",
            joinColumns = @JoinColumn(name = "taskId"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private UserEntity creator;
}

