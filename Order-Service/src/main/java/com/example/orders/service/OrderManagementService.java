package com.example.orders.service;

import com.example.orders.entity.OrderEntity;
import com.example.orders.exception.OrderNotFoundException;
import com.example.orders.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderManagementService {
    private final OrderRepository orderRepository;

    public OrderManagementService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    public OrderEntity getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found: " + orderId));
    }
    public Page<OrderEntity> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
