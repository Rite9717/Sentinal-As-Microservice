package com.sentinal.monitoring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "latest_metrics")
public class LatestMetrics
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long instanceDbId;
    private String instanceId;
    private String region;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Boolean valid;
    private String  errorMessage;
    private LocalDateTime collectedAt;
    private LocalDateTime updatedAt;
}
