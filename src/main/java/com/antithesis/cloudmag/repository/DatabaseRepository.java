package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.DatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatabaseRepository extends JpaRepository<DatabaseEntity, UUID> {
    Boolean existsByName(String username);

    List<DatabaseEntity> findAll();

    DatabaseEntity findByName(String databaseName);
}
