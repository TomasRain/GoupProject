package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    /**
     * 受保护的 Hello 接口，只有经过认证的用户可以访问
     *
     * @return 欢迎消息
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, authenticated user!";
    }
}
