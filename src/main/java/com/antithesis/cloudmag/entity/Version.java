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
@Table(	name = "versions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "version_id"),
                @UniqueConstraint(columnNames = "name")
        })
@Builder
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID version_id;

    @NotBlank
    private String creator;

    private Long created_at;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String tag_name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "project_versions",
            joinColumns = @JoinColumn(name = "version_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private Project project_id = new Project();
}

