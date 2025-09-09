package com.example.pos_backend.service;

import com.example.pos_backend.dto.OrderRequest;
import com.example.pos_backend.entity.Order;
import com.example.pos_backend.entity.OrderItem;
import com.example.pos_backend.entity.Product;
import com.example.pos_backend.repository.OrderRepository;
import com.example.pos_backend.repository.OrderItemRepository;
import com.example.pos_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository productRepository;

    public Order createOrder(OrderRequest req) {
        Order order = Order.builder()
                .orgId(req.getOrgId())
                .storeId(req.getStoreId())
                .userId(req.getUserId())
                .status(Order.Status.CREATED)
                .currency("USD")
                .totalAmount(BigDecimal.ZERO)
                .build();
        final Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = req.getItems().stream().map(i -> {
            Product product = productRepository.findById(i.getProductId()).orElseThrow();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()));

            return OrderItem.builder()
                    .order(savedOrder)
                    .orgId(req.getOrgId())
                    .storeId(req.getStoreId())
                    .productId(i.getProductId())
                    .quantity(i.getQuantity())
                    .price(product.getPrice())
                    .currency("USD")
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());

        // Calculate total from items
        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        savedOrder.setTotalAmount(total);
        orderRepository.save(savedOrder);
        orderItemRepository.saveAll(items);

        savedOrder.setItems(items);
        return savedOrder;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }
}
