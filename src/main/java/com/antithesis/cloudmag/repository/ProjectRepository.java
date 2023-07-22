package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    //@Query("SELECT p FROM ProjectEntity p WHERE p.creator = ?1")
    List<ProjectEntity> findAll();

    void deleteByName(String project_name);

    Boolean existsByName(String email);
}
