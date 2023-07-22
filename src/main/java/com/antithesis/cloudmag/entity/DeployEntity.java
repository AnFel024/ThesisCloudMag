package com.antithesis.cloudmag.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(	name = "deploys",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "deployId")
        })
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeployEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deployId;

    private Long createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "version_instances",
            joinColumns = @JoinColumn(name = "deployId"),
            inverseJoinColumns = @JoinColumn(name = "instanceId"))
    private InstanceEntity instanceInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "deploy_version",
            joinColumns = @JoinColumn(name = "deployId"),
            inverseJoinColumns = @JoinColumn(name = "versionId"))
    private VersionEntity versionInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "deploy_user",
            joinColumns = @JoinColumn(name = "deployId"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private UserEntity creator;
}

