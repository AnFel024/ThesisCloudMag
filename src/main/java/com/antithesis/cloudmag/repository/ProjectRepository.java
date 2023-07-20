package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.constant.RoleTypes;
import com.antithesis.cloudmag.entity.Project;
import com.antithesis.cloudmag.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.user = ?1 AND t.bar = ?2")
    Project findByUserOwner(String userOwner);
}
