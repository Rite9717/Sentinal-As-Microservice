package com.sentinal.instance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;

@Configuration
public class AwsConfig {

    @Value("${sentinal.aws.region}")
    private String awsRegion;

    @Bean
    public StsClient stsClient() {
        return StsClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}