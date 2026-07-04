package com.sentinal.auth.dto;

import lombok.Data;

@Data
public class Register
{
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
