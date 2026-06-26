package com.sentinal.auth.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table( name = "user")
public class User
{
    private Long id;
    private String username;
    private String password;
    private String email;
    
}
