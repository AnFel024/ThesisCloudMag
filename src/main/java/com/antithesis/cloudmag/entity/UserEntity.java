package com.antithesis.cloudmag.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
                @UniqueConstraint(columnNames = "email")
        })
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserEntity {
    @Id
    @NotBlank
    @Size(max = 20)
    private String id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId"))
    private Set<RoleEntity> roleEntities;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_database",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "databaseId"))
    private Set<DatabaseEntity> databasesCreated;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_deploys",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "deployId"))
    private Set<DeployEntity> deploysCreated;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_projects",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "projectId"))
    private Set<ProjectEntity> projectsCreated;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_tasks",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "taskId"))
    private Set<TaskEntity> tasksCreated;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_tasks",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "versionId"))
    private Set<VersionEntity> versionsCreated;

    public UserEntity(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
}