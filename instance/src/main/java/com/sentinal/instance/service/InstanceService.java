package com.sentinal.instance.service;

import com.sentinal.instance.dto.IamRoleRequest;
import com.sentinal.instance.dto.MonitoringConfigRequest;
import com.sentinal.instance.dto.Request;
import com.sentinal.instance.dto.Response;
import com.sentinal.instance.model.InstanceEntity;
import com.sentinal.instance.repository.InstanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InstanceService
{
    private final InstanceRepository instanceRepository;
    private final AwsStsService awsStsService;
    private final MonitoringVerificationService monitoringVerificationService;
    InstanceService(InstanceRepository instanceRepository, AwsStsService awsStsService, MonitoringVerificationService monitoringVerificationService)
    {
        this.instanceRepository = instanceRepository;
        this.awsStsService = awsStsService;
        this.monitoringVerificationService = monitoringVerificationService;
    }

    public Response registerInstance(Long ownerUserId, Request request)
    {
        boolean exists = instanceRepository.existsByInstanceIdAndOwnerUserId(request.getInstanceId(), ownerUserId);
        if (exists)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Instance already exists");
        }
        InstanceEntity instance = new InstanceEntity();
        instance.setInstanceId(request.getInstanceId());
        instance.setOwnerUserId(ownerUserId);
        instance.setAwsAccountId(request.getAwsAccountId());
        instance.setRegion(request.getRegion());
        instance.setName(request.getName());
        instance.setState("REGISTERED");
        instance.setCreatedAt(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());

        instance = instanceRepository.save(instance);
        return toResponse(instance);
    }

    public List<Response> listInstances(Long ownerUserId)
    {
        return instanceRepository.findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Response startIamSetup(Long instanceId, Long ownerId)
    {
        InstanceEntity instance = findOwnedInstance(instanceId, ownerId);
        instance.setOnboardingState("IAM_SETUP_PENDING");
        instance.setUpdatedAt(LocalDateTime.now());
        return toResponse(instanceRepository.save(instance));
    }

    public Response saveIamRole(Long instanceId, Long ownerId, IamRoleRequest request) {
        InstanceEntity instance = findOwnedInstance(instanceId, ownerId);

        instance.setRoleArn(request.getRoleArn());
        instance.setExternalId(request.getExternalId());
        instance.setOnboardingState("IAM_ROLE_SUBMITTED");
        instance.setUpdatedAt(LocalDateTime.now());

        return toResponse(instanceRepository.save(instance));
    }

    public Response verifyIamRole(Long instanceId, Long ownerId)
    {
        InstanceEntity instance = findOwnedInstance(instanceId, ownerId);
        if(instance.getRoleArn()== null || instance.getRoleArn().isBlank())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IAM role ARN is required before verification");
        }

        try {
            awsStsService.verifyAssumeRole(instance.getRoleArn(), instance.getExternalId());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Unable to assume IAM role: " + e.getMessage()
            );
        }

        instance.setOnboardingState("IAM_ROLE_VERIFIED");
        instance.setUpdatedAt(LocalDateTime.now());
        return toResponse(instanceRepository.save(instance));
    }

    public Response saveMonitoringConfig(Long instanceId, Long ownerId, MonitoringConfigRequest request)
    {
        InstanceEntity instance = findOwnedInstance(instanceId, ownerId);
        if (!"IAM_ROLE_VERIFIED".equals(instance.getOnboardingState())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "IAM role must be verified before configuring monitoring"
            );
        }
        instance.setPrometheusUrl(request.getPrometheusUrl());
        instance.setGrafanaUrl(request.getGrafanaUrl());
        instance.setNodeExporterStatus(request.getNodeExporterStatus());
        instance.setOnboardingState("MONITORING_SUBMITTED");
        instance.setUpdatedAt(LocalDateTime.now());

        return toResponse(instanceRepository.save(instance));
    }

    public Response verifyMonitoring(Long instanceId, Long ownerId) {
        InstanceEntity instance = findOwnedInstance(instanceId, ownerId);

        if (!"MONITORING_SUBMITTED".equals(instance.getOnboardingState())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Monitoring config must be submitted before verification"
            );
        }

        monitoringVerificationService.verifyMonitoringStack(
                instance.getPrometheusUrl(),
                instance.getGrafanaUrl()
        );

        instance.setNodeExporterStatus("VERIFIED");
        instance.setOnboardingState("READY_FOR_MONITORING");
        instance.setUpdatedAt(LocalDateTime.now());

        return toResponse(instanceRepository.save(instance));
    }

    public Response getOnboarding(Long instanceId, Long ownerId)
    {
        return toResponse(findOwnedInstance(instanceId, ownerId));
    }

    private InstanceEntity findOwnedInstance(Long instanceId, Long ownerId)
    {
        return instanceRepository
                .findByIdAndOwnerUserId(instanceId, ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instance not found"));
    }

    public List<Response> listReadyForMonitoring() {
        return instanceRepository.findByOnboardingStateOrderByCreatedAtDesc("READY_FOR_MONITORING")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Response toResponse(InstanceEntity instance)
    {
        Response response = new Response();
        response.setId(instance.getId());
        response.setAwsAccountId(instance.getAwsAccountId());
        response.setInstanceId(instance.getInstanceId());
        response.setRegion(instance.getRegion());
        response.setName(instance.getName());
        response.setState(instance.getState());
        response.setCreatedAt(instance.getCreatedAt());
        response.setOnboardingState(instance.getOnboardingState());
        response.setRoleArn(instance.getRoleArn());
        response.setExternalId(instance.getExternalId());
        response.setPrometheusUrl(instance.getPrometheusUrl());
        response.setGrafanaUrl(instance.getGrafanaUrl());
        response.setNodeExporterStatus(instance.getNodeExporterStatus());
        return response;
    }
}
