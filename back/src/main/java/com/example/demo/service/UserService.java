package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 注释掉密码编码器的导入
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    // private final BCryptPasswordEncoder passwordEncoder; // 注释掉密码编码器

    // 构造器注入（移除密码编码器）
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // this.passwordEncoder = passwordEncoder;
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
        // 直接存储明文密码
        user.setPassword(password);
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
