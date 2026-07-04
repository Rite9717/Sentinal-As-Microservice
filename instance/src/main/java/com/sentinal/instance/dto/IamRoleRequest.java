package com.sentinal.instance.dto;

import lombok.Data;

@Data
public class IamRoleRequest
{
    private String roleArn;
    private String externalId;
}
