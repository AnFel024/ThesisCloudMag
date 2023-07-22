package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.DatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatabaseRepository extends JpaRepository<DatabaseEntity, Long> {
    Boolean existsByName(String username);

    List<DatabaseEntity> findAll();
}
