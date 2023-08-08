package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.StatusEntity;
import com.antithesis.cloudmag.entity.VersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, UUID> {}
