package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 构造器注入
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 用户注册
    public UserDTO register(String username, String password) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("用户名已存在");
        }
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 密码加密在 User 类中处理
        User savedUser = userRepository.save(user);
        // 返回 DTO
        return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getRole().name());
    }

    // 根据用户名查找用户
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
