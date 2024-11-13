package com.example.demo.dto;

public class UserDTO {
    private Long id;
    private String username;
    private String role;

    public UserDTO(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Getter 方法
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
