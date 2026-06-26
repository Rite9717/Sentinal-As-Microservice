package com.sentinal.auth.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table( name = "user")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = false)
    private String email;

    @Column(nullable = true)
    private String token;

    
}
