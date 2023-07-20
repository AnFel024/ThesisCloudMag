package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.creator = ?1")
    Project findByUserOwner(String userOwner);
}
