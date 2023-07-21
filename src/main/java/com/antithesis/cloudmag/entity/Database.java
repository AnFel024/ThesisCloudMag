package com.antithesis.cloudmag.entity;

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
                @UniqueConstraint(columnNames = "id"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
public class Database {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String dbms;

    @NotBlank
    private String creator;

    @NotBlank
    private Long created_at;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "database_instances",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "instance_id"))
    private Instance instance_info = new Instance();
}

