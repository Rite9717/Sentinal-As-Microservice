package com.sentinal.instance.dto;

import lombok.Data;

@Data
public class Request
{
    private  String awsAccountId;
    private String instanceId;
    private  String region;
    private String name;
}
