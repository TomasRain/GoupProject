package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.model.User;
import com.example.demo.model.User.Role; // 导入 Role 枚举
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 构造器注入
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 用户注册
    public UserDTO register(String username, String password, String roleStr) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("用户名已存在");
        }

        // 将传递的角色字符串转换为枚举类型
        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 如果角色不匹配枚举，抛出异常或设置为默认角色
            role = Role.USER; // 或者抛出异常
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        User savedUser = userRepository.save(user);
        // 返回 DTO
        return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getRole().name());
    }

    // 根据用户名查找用户
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 获取所有用户
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
