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
@Table(	name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "projectId"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID projectId;

    @NotBlank
    private String name;

    @NotBlank
    private String repositoryUrl;

    private Long createdAt;

    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "project_instances",
            joinColumns = @JoinColumn(name = "projectId"),
            inverseJoinColumns = @JoinColumn(name = "instanceId"))
    private InstanceEntity instanceInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "project_user",
            joinColumns = @JoinColumn(name = "projectId"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private UserEntity creator;
}

