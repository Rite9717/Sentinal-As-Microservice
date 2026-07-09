package com.sentinal.monitoring.service;

import com.sentinal.monitoring.dto.InstanceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class InstanceClient {

    private final RestClient restClient;

    @Value("${sentinal.services.instance-service-url}")
    private String instanceServiceUrl;

    public InstanceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<InstanceDto> getReadyInstances() {
        return restClient.get()
                .uri(instanceServiceUrl + "/api/instances/internal/ready-for-monitoring")
                .retrieve()
                .body(new ParameterizedTypeReference<List<InstanceDto>>() {});
    }
}