package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.VersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VersionRepository extends JpaRepository<VersionEntity, UUID> {
    @Query("SELECT v FROM VersionEntity v WHERE v.projectInfo.name = ?1")
    List<VersionEntity> findAllByProjectInfoName(String name);
    List<VersionEntity> findAll();
}
