package com.sentinal.instance.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Response
{
    private Long id;
    private String instanceId;
    private String awsAccountId;
    private String region;
    private String name;
    private String state;
    private LocalDateTime createdAt;
    private String onboardingState;
    private String roleArn;
    private String externalId;
    private String prometheusUrl;
    private String grafanaUrl;
    private String nodeExporterStatus;
}
