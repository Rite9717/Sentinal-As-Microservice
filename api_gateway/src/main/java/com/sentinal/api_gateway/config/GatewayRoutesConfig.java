package com.sentinal.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    RouteLocator sentinalRoutes(
            RouteLocatorBuilder routes,
            @Value("${sentinal.gateway.services.auth-url}") String authUrl,
            @Value("${sentinal.gateway.services.instance-url}") String instanceUrl,
            @Value("${sentinal.gateway.services.monitoring-url}") String monitoringUrl,
            @Value("${sentinal.gateway.services.incident-url}") String incidentUrl,
            @Value("${sentinal.gateway.services.ai-url}") String aiUrl
    ) {
        return routes.routes()
                .route("auth-service", route -> route
                        .path("/api/auth/**", "/oauth2/**", "/login/**")
                        .uri(authUrl))
                .route("instance-service", route -> route
                        .path("/api/instances/**", "/api/agent/tools/instances/**")
                        .uri(instanceUrl))
                .route("monitoring-service", route -> route
                        .path("/api/metrics/**", "/api/monitoring/**", "/ws/**")
                        .uri(monitoringUrl))
                .route("incident-service", route -> route
                        .path("/api/incidents/**", "/api/snapshots/**")
                        .uri(incidentUrl))
                .route("sentinal-ai", route -> route
                        .path("/agent/**", "/api/ai/**")
                        .uri(aiUrl))
                .build();
    }
}
