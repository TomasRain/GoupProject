package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 注册接口
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        Optional<User> existingUser = userService.findByUsername(username);
        if (existingUser.isPresent()) {
            return "User already exists!";
        }
        userService.register(username, password);
        return "User registered successfully!";
    }

    // 登录接口
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return "Login successful!";
        } else {
            return "Invalid username or password!";
        }
    }
}
