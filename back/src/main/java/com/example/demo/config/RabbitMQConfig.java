package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 定义队列
    @Bean
    public Queue orderQueue() {
        return new Queue("orderQueue");
    }

    // 定义交换机
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("orderExchange");
    }

    // 绑定队列和交换机
    @Bean
    public Binding bindingOrderQueue(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with("orderRoutingKey");
    }
}
