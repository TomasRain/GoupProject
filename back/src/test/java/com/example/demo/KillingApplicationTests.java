package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KillingApplication.class) // 明确指定主类
public class KillingApplicationTests {

    @Test
    void contextLoads() {
        // 验证 Spring 应用上下文是否可以正常加载
    }
}
