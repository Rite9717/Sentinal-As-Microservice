package com.sentinal.auth.controller;

import com.sentinal.auth.dto.Login;
import com.sentinal.auth.dto.Register;
import com.sentinal.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test()
    {
        return "Auth OK";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register)
    {
        return ResponseEntity.ok(authService.register(register));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login)
    {

        return ResponseEntity.ok(authService.login(login));
    }
}
