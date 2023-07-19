package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.constant.RoleTypes;
import com.antithesis.cloudmag.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleTypes name);
}
