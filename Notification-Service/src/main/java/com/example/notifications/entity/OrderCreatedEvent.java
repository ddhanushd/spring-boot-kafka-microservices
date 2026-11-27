package com.example.notifications.entity;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String eventId,
        String orderId,
        String customerEmail,
        BigDecimal amount
) {}
