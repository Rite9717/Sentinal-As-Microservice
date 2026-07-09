package com.sentinal.monitoring.scheduler;

import com.sentinal.monitoring.dto.InstanceDto;
import com.sentinal.monitoring.dto.MetricValues;
import com.sentinal.monitoring.service.InstanceClient;
import com.sentinal.monitoring.service.LatestMetricsService;
import com.sentinal.monitoring.service.PrometheusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MonitoringScheduler {

    private final InstanceClient instanceClient;
    private final PrometheusService prometheusService;
    private final LatestMetricsService latestMetricsService;

    public MonitoringScheduler(
            InstanceClient instanceClient,
            PrometheusService prometheusService,
            LatestMetricsService latestMetricsService
    ) {
        this.instanceClient = instanceClient;
        this.prometheusService = prometheusService;
        this.latestMetricsService = latestMetricsService;
    }

    @Scheduled(fixedDelayString = "${sentinal.monitoring.fixed-delay-ms}")
    public void monitorReadyInstances() {
        List<InstanceDto> instances = instanceClient.getReadyInstances();

        log.info("Monitoring tick. Ready instances: {}", instances.size());

        for (InstanceDto instance : instances) {
            MetricValues metrics = prometheusService.collectMetrics(instance);
            latestMetricsService.upsert(instance, metrics);

            log.info(
                    "Metrics collected for {} valid={} cpu={} memory={} disk={}",
                    instance.getInstanceId(),
                    metrics.getValid(),
                    metrics.getCpuUsage(),
                    metrics.getMemoryUsage(),
                    metrics.getDiskUsage()
            );
        }
    }
}