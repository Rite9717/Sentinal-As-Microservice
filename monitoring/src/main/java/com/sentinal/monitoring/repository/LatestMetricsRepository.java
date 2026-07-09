package com.sentinal.monitoring.repository;

import com.sentinal.monitoring.model.LatestMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LatestMetricsRepository extends JpaRepository<LatestMetrics, Long>
{
    Optional<LatestMetrics> findByInstanceDbId(Long instanceDbId);
}
