package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.Database;
import com.antithesis.cloudmag.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    List<Version> findAll();
}
