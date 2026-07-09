package com.sentinal.monitoring.service;

import com.sentinal.monitoring.dto.InstanceDto;
import com.sentinal.monitoring.dto.MetricValues;
import com.sentinal.monitoring.model.LatestMetrics;
import com.sentinal.monitoring.repository.LatestMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LatestMetricsService {

    private final LatestMetricsRepository repository;

    public LatestMetricsService(LatestMetricsRepository repository) {
        this.repository = repository;
    }

    public LatestMetrics upsert(InstanceDto instance, MetricValues metrics) {
        LatestMetrics latest = repository.findByInstanceDbId(instance.getId())
                .orElseGet(LatestMetrics::new);

        latest.setInstanceDbId(instance.getId());
        latest.setInstanceId(instance.getInstanceId());
        latest.setRegion(instance.getRegion());

        latest.setCpuUsage(metrics.getCpuUsage());
        latest.setMemoryUsage(metrics.getMemoryUsage());
        latest.setDiskUsage(metrics.getDiskUsage());

        latest.setValid(metrics.getValid());
        latest.setErrorMessage(metrics.getErrorMessage());
        latest.setCollectedAt(LocalDateTime.now());
        latest.setUpdatedAt(LocalDateTime.now());

        return repository.save(latest);
    }
}