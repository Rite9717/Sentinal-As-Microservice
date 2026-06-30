package com.sentinal.auth.service;

import com.sentinal.auth.dto.Login;
import com.sentinal.auth.dto.Register;
import com.sentinal.auth.dto.Response;
import com.sentinal.auth.model.User;
import com.sentinal.auth.repository.UserRepository;
import com.sentinal.auth.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Response register(Register request)
    {
        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getFirstName(),
                user.getRole()
        );

        return new Response(token, user.getEmail(), user.getFirstName(),user.getId());
    }

    public Response login(Login request)
    {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getFirstName(),
                user.getRole()
        );

        return new Response(token, user.getEmail(), user.getFirstName(), user.getId());
    }
}
