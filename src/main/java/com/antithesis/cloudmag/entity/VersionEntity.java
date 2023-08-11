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
@Table(	name = "versions")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VersionEntity {
    @Id
    private UUID id;
    private Long createdAt;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String tagName;
    private String branchName;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "version_users",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "userId"))
    private UserEntity creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "version_projects",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "projectId"))
    private ProjectEntity projectInfo;
}

