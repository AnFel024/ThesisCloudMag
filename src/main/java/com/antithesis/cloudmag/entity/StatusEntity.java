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
@Table(	name = "status")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String statusName;
    private Long updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(	name = "status_users",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "userId"))
    private UserEntity updatedBy;
}
