package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.creator = ?1")
    List<Project> findByUserOwner(String userOwner);

    void deleteByName(String project_name);

    Boolean existsByName(String email);
}
