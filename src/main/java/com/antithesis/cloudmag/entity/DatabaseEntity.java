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
@Table(	name = "databases")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DatabaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String dbms;

    @NotBlank
    private Long createdAt;

    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "database_instances",
            joinColumns = @JoinColumn(name = "databaseId"),
            inverseJoinColumns = @JoinColumn(name = "instanceId"))
    private InstanceEntity instanceInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "database_users",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "userId"))
    private UserEntity creator;
}

