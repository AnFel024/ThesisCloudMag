package com.antithesis.cloudmag.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(	name = "instances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "instance_id"),
                @UniqueConstraint(columnNames = "name")
        })
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Instance {
    @Id
    private String instance_id;

    @Size(max = 20)
    private String name;

    @Size(max = 20)
    private String type;

    @NotBlank
    @Size(max = 50)
    private String provider;

    @Size(max = 120)
    private String host_url;

    private String reservation_id;
}
