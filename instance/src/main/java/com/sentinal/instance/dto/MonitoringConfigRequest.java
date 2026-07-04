package com.sentinal.instance.dto;

import lombok.Data;

@Data
public class MonitoringConfigRequest
{
    private String prometheusUrl;
    private String grafanaUrl;
    private String nodeExporterStatus;
}
