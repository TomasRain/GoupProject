package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderRequest;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderDTO orderDTO = orderService.createOrder(
                orderRequest.getUserId(),
                orderRequest.getProductId(),
                orderRequest.getQuantity()
        );
        return ResponseEntity.ok(orderDTO);
    }
}
