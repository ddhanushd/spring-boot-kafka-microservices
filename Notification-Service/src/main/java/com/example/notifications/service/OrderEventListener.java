package com.example.notifications.service;

import com.example.notifications.entity.OrderCreatedEvent;
import com.example.notifications.entity.ProcessedEvent;
import com.example.notifications.repository.ProcessedEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderEventListener {

    private final ProcessedEventRepository repository;

    public OrderEventListener(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "orders.created")
    public void handleOrderCreated(OrderCreatedEvent event) {

        if (repository.existsByEventId(event.eventId())) {
            System.out.println("Duplicate event detected: " + event.eventId());
            return;
        }

        System.out.println("================================");
        System.out.println("New order received!");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Order ID: " + event.orderId());
        System.out.println("Customer: " + event.customerEmail());
        System.out.println("Amount: " + event.amount());

        System.out.println("Sending order confirmation...");

        //throw new RuntimeException("Simulated notification failure");
         //Save only after processing succeeds
        repository.save(
                new ProcessedEvent(event.eventId(), LocalDateTime.now())
        );

        System.out.println("================================");
    }

}
