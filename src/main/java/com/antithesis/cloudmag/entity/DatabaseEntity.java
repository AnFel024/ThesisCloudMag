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
@Table(	name = "databases",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "databaseId"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DatabaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID databaseId;

    @NotBlank
    private String name;

    @NotBlank
    private String dbms;

    @NotBlank
    private Long createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "database_instances",
            joinColumns = @JoinColumn(name = "databaseId"),
            inverseJoinColumns = @JoinColumn(name = "instanceId"))
    private InstanceEntity instanceInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "database_user",
            joinColumns = @JoinColumn(name = "databaseId"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private UserEntity creator;
}

