package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.VersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<VersionEntity, Long> {
    List<VersionEntity> findAll();
}
