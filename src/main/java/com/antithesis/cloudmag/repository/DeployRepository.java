package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.DeployEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployRepository extends JpaRepository<DeployEntity, Long> {
    @Query("SELECT v FROM DeployEntity v WHERE v.versionInfo.projectInfo.name = ?1")
    List<DeployEntity> findAllByProjectInfoName(String name);

    List<DeployEntity> findAll();
}
