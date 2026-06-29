package com.sentinal.auth.Controller;

import com.sentinal.auth.DTO.Login;
import com.sentinal.auth.DTO.Register;
import com.sentinal.auth.DTO.Response;
import com.sentinal.auth.Model.User;
import com.sentinal.auth.Repository.UserRepository;
import com.sentinal.auth.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/test")
    public String test()
    {
        return "OK";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) {
        Optional<User> existingUser = userRepository.findByEmail(register.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = new User();
        user.setEmail(register.getEmail());
        user.setUsername(register.getEmail());
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setToken(null);
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getFirstName());
        user.setToken(token);
        userRepository.save(user);

        Response response = new Response(token, user.getEmail(), user.getFirstName(), user.getId());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login)
    {
        Optional<User> finduser=userRepository.findByEmail(login.getEmail());
        if(finduser.isEmpty())
        {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user=finduser.get();

        if(!passwordEncoder.matches(login.getPassword(),user.getPassword()))
        {
            return ResponseEntity.badRequest().body("Incorrect Password");
        }
        user.setToken(null);
        String token=jwtUtil.generateToken(user.getEmail(),user.getId(),user.getFirstName());
        user.setToken(token);
        userRepository.save(user);

        Response response=new Response(token,user.getEmail(),user.getFirstName(),user.getId());
        return ResponseEntity.ok().body(response);
    }
}
