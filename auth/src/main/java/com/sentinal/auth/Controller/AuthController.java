package com.sentinal.auth.Controller;

import com.sentinal.auth.DTO.Login;
import com.sentinal.auth.DTO.Register;
import com.sentinal.auth.Service.AuthService;
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
