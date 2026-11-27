
package com.example.orders.controller;

import com.example.orders.dto.OrderRequest;
import com.example.orders.entity.OrderCreatedEvent;
import com.example.orders.entity.OrderEntity;
import com.example.orders.service.OrderManagementService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final OrderManagementService orderManagementService;

    public OrderController(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            OrderManagementService orderManagementService) {

        this.kafkaTemplate = kafkaTemplate;
        this.orderManagementService = orderManagementService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(
            @Valid @RequestBody OrderRequest request) {

        String orderId = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();

        OrderEntity order = new OrderEntity();
        order.setOrderId(orderId);
        order.setCustomerEmail(request.getEmail());
        order.setAmount(request.getAmount());
        order.setStatus("CREATED");

        orderManagementService.saveOrder(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId,
                orderId,
                request.getEmail(),
                request.getAmount()
        );

        kafkaTemplate.send("orders.created", orderId, event);

        return ResponseEntity.accepted()
                .body("Order accepted: " + orderId);
    }

    @GetMapping
    public ResponseEntity<Page<OrderEntity>> getAllOrders(
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {

        Page<OrderEntity> orders =
                orderManagementService.getAllOrders(pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderEntity> getOrderByOrderId(
            @PathVariable String orderId) {

        OrderEntity order =
                orderManagementService.getOrderByOrderId(orderId);

        return ResponseEntity.ok(order);
    }
}
