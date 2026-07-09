package com.sentinal.instance.config;

import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sts.StsClient;

@Configuration
public class AwsConfig
{
    public StsClient stsClient()
    {
        return StsClient.create();
    }
}
