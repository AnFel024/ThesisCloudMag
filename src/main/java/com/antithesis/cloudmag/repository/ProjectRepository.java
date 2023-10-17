package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    //@Query("SELECT p FROM ProjectEntity p WHERE p.creator = ?1")
    List<ProjectEntity> findAll();

    Optional<ProjectEntity> findByName(String project_name);

    void deleteByName(String project_name);

    Boolean existsByName(String email);
}
