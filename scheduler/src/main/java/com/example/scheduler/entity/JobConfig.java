package com.example.scheduler.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "quartz_job_config")
@Data
@Where(clause = "deleted_date is null")
public class JobConfig extends AbstractDate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobName;
    private String cronExpression;
    private String jobClass;
}
