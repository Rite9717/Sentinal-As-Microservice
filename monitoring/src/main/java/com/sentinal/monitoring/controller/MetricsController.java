package com.sentinal.monitoring.controller;

import com.sentinal.monitoring.model.LatestMetrics;
import com.sentinal.monitoring.repository.LatestMetricsRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final LatestMetricsRepository repository;

    public MetricsController(LatestMetricsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/instances/{instanceDbId}/latest")
    public LatestMetrics latest(@PathVariable Long instanceDbId) {
        return repository.findByInstanceDbId(instanceDbId)
                .orElseThrow(() -> new RuntimeException("Metrics not found"));
    }
}