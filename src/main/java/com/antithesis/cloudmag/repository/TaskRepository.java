package com.antithesis.cloudmag.repository;

import com.antithesis.cloudmag.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Boolean existsByName(String username);

    void deleteByName(String task_name);
    List<TaskEntity> findAll();
}
