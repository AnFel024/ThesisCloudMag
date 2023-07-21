package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.Database;
import com.antithesis.cloudmag.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    Boolean existsByName(String username);

    List<Database> findAll();
}
