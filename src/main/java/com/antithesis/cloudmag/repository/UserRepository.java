package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findById(String username);

    Boolean existsByEmail(String email);

    List<UserEntity> findAll();
}
