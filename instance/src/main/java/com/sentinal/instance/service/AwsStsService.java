package com.sentinal.instance.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.StsException;

@Service
public class AwsStsService
{
    private final StsClient stsClient;

    public AwsStsService(StsClient stsClient)
    {
        this.stsClient = stsClient;
    }

    public void verifyAssumeRole(String roleArn, String externalId)
    {
        try
        {
            AssumeRoleRequest.Builder builder = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName("sentinal-verify-session");
            if(externalId !=null && !externalId.isBlank())
            {
                builder.externalId(externalId);
            }

            stsClient.assumeRole(builder.build());
        }
        catch (StsException ignored)
        {
            throw ignored;
        }
    }
}
