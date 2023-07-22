package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    //@Query("SELECT p FROM ProjectEntity p WHERE p.creator = ?1")
    List<ProjectEntity> findAllByCreator(UserEntity userOwner);

    void deleteByName(String project_name);

    Boolean existsByName(String email);
}
