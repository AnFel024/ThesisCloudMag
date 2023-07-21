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
@Table(	name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "project_id"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID project_id;

    @NotBlank
    private String name;

    @NotBlank
    private String repository_url;

    @NotBlank
    private String creator;

    private Long created_at;

    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "project_instances",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "instance_id"))
    private Instance instance_info = new Instance();
}

