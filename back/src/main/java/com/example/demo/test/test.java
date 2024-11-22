package com.example.demo.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "root"; // 您希望设置的管理员密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("加密后的密码：" + encodedPassword);
    }
}