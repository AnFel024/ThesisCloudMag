package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {
    List<InstanceEntity> findAll();
}
