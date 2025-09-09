package com.example.pos_backend.controller;

import com.example.pos_backend.entity.Order;
import com.example.pos_backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired private OrderRepository orderRepository;

    // 模拟支付接口
    @PostMapping("/{orderId}")
    public String pay(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != Order.Status.CREATED) {
            return "Order not payable";
        }
        order.setStatus(Order.Status.PAID);
        orderRepository.save(order);
        return "Payment successful (mock)";
    }
}
