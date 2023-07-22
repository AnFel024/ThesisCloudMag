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
@Table(	name = "versions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "versionId"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID versionId;

    private Long createdAt;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String tagName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "version_projects",
            joinColumns = @JoinColumn(name = "versionId"),
            inverseJoinColumns = @JoinColumn(name = "projectId"))
    private ProjectEntity projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "version_user",
            joinColumns = @JoinColumn(name = "versionId"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private UserEntity creator;
}

