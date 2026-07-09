package com.sentinal.monitoring.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricValues
{
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Boolean valid;
    private String errorMessage;
}
