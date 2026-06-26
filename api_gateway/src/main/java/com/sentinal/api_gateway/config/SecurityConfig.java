package com.sentinal.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            @Value("${sentinal.gateway.security.jwt-enabled:false}") boolean jwtEnabled
    ) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        http.authorizeExchange(exchanges -> {
            exchanges.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            exchanges.pathMatchers("/actuator/health", "/actuator/info").permitAll();
            exchanges.pathMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll();
            if (jwtEnabled) {
                exchanges.anyExchange().authenticated();
            } else {
                exchanges.anyExchange().permitAll();
            }
        });

        if (jwtEnabled) {
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> { }));
        }

        return http.build();
    }
}
