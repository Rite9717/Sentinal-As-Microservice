package com.sentinal.instance.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "instances")
public class InstanceEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerUserId;
    private String awsAccountId;
    private String instanceId;
    private String region;
    private String name;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String onboardingState = "REGISTERED";
    private String roleArn;
    private String externalId;
    private String prometheusUrl;
    private String grafanaUrl;
    private String nodeExporterStatus;
}
