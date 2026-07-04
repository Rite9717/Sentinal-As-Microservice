package com.sentinal.auth.dto;

import lombok.Data;

@Data
public class Login
{
    private String email;
    private String username;
    private String password;
}
