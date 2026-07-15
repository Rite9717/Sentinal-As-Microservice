package com.sentinal.monitoring.service;

import com.sentinal.monitoring.dto.InstanceDto;
import com.sentinal.monitoring.dto.MetricValues;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class PrometheusService {

    private final RestClient restClient;

    public PrometheusService(RestClient restClient) {
        this.restClient = restClient;
    }

    public MetricValues collectMetrics(InstanceDto instance) {
        try {
            if (instance.getPrometheusUrl() == null || instance.getPrometheusUrl().isBlank()) {
                return MetricValues.builder()
                        .valid(false)
                        .errorMessage("Prometheus URL is not configured")
                        .build();
            }

            Double cpu = query(instance.getPrometheusUrl(),
                    "100 * (1 - avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m])))");

            Double memory = query(instance.getPrometheusUrl(),
                    "100 * (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes))");

            Double disk = query(instance.getPrometheusUrl(),
                    "100 * (1 - (node_filesystem_avail_bytes{mountpoint=\"/\",fstype!=\"tmpfs\"} / node_filesystem_size_bytes{mountpoint=\"/\",fstype!=\"tmpfs\"}))");

            return MetricValues.builder()
                    .cpuUsage(cpu)
                    .memoryUsage(memory)
                    .diskUsage(disk)
                    .valid(cpu != null || memory != null || disk != null)
                    .build();

        } catch (Exception exception) {
            return MetricValues.builder()
                    .valid(false)
                    .errorMessage(exception.getMessage())
                    .build();
        }
    }

    private Double query(String prometheusUrl, String promql) {
        Map response = restClient.get()
                .uri(prometheusUrl.replaceAll("/+$", "") + "/api/v1/query?query={query}", promql)
                .retrieve()
                .body(Map.class);

        try {
            if (response == null) {
                return null;
            }
            Map data = (Map) response.get("data");
            var result = (java.util.List<?>) data.get("result");

            if (result == null || result.isEmpty()) {
                return null;
            }

            Map first = (Map) result.get(0);
            var value = (java.util.List<?>) first.get("value");

            return Double.parseDouble(value.get(1).toString());
        } catch (Exception exception) {
            return null;
        }
    }
}
