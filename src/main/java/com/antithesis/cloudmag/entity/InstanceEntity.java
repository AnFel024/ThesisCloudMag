package com.antithesis.cloudmag.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(	name = "instances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "instanceId"),
                @UniqueConstraint(columnNames = "name")
        })
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InstanceEntity {
    @Id
    private String instanceId;

    @Size(max = 20)
    private String name;

    @Size(max = 20)
    private String type;

    @NotBlank
    @Size(max = 50)
    private String provider;

    @Size(max = 120)
    private String hostUrl;

    private String reservationId;
}
