package com.sentinal.monitoring.dto;

import lombok.Data;

@Data
public class InstanceDto
{
    private Long id;
    private String instanceId;
    private String awsAccountId;
    private String region;
    private String name;
    private String state;
    private String onboardingState;
    private String prometheusUrl;
    private String grafanaUrl;
    private String nodeExporterStatus;
}
