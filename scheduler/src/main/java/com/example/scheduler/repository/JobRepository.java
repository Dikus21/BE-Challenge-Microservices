package com.example.scheduler.repository;

import com.example.scheduler.entity.JobConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobConfig, Long> {

}
