package com.sentinal.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig
{
    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder(@Value("${sentinal.jwt.secret}") String secret)
    {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}
