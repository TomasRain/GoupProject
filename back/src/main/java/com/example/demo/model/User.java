package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
//不能用javax！！！！重要的事情反复强调
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "users")  // 数据库中的表名应为 users
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "密码不能为空")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.USER;

    // 定义角色枚举
    public enum Role {
        USER,
        ADMIN
        // 可以根据需要添加更多角色
    }

    // 无参构造函数
    public User() {}

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    // setId 方法通常不需要，因为 ID 是自动生成的

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // 设置密码时进行加密
    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
