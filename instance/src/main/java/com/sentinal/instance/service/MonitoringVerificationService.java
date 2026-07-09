package com.sentinal.instance.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class MonitoringVerificationService {

    private final RestClient restClient;

    public MonitoringVerificationService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void verifyPrometheus(String prometheusUrl) {
        if (prometheusUrl == null || prometheusUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prometheus URL is required");
        }

        String url = trimTrailingSlash(prometheusUrl) + "/-/ready";

        try {
            restClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Prometheus is not ready. Status: " + response.getStatusCode()
                        );
                    })
                    .toBodilessEntity();
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to verify Prometheus: " + exception.getMessage()
            );
        }
    }

    public void verifyGrafana(String grafanaUrl) {
        if (grafanaUrl == null || grafanaUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grafana URL is required");
        }

        String url = trimTrailingSlash(grafanaUrl) + "/api/health";

        try {
            restClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Grafana is not healthy. Status: " + response.getStatusCode()
                        );
                    })
                    .toBodilessEntity();
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to verify Grafana: " + exception.getMessage()
            );
        }
    }

    public void verifyMonitoringStack(String prometheusUrl, String grafanaUrl) {
        verifyPrometheus(prometheusUrl);
        verifyGrafana(grafanaUrl);
    }

    private String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}