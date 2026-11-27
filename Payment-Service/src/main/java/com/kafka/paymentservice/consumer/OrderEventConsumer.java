package com.kafka.paymentservice.consumer;


import com.kafka.paymentservice.entity.Payment;
import com.kafka.paymentservice.event.OrderCreatedEvent;
import com.kafka.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderEventConsumer {

    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @KafkaListener(topics = "orders.created", groupId = "payment-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        Payment payment = new Payment();
        payment.setOrderId(event.orderId());
        payment.setAmount(event.amount());
        payment.setEventId(event.eventId());

        Payment savedPayment = paymentService.processPayment(payment);
        if (payment.getAmount().compareTo(new BigDecimal("999999")) > 0) {
            throw new RuntimeException("Testing Kafka retry and DLT");
        }

        System.out.println("Payment processed successfully!");
        System.out.println("Payment ID: " + savedPayment.getId());
        System.out.println("Order ID: " + savedPayment.getOrderId());
        System.out.println("Amount: " + savedPayment.getAmount());
        System.out.println("Event ID: " + savedPayment.getEventId());
        System.out.println("Status: " + savedPayment.getStatus());
    }
}
