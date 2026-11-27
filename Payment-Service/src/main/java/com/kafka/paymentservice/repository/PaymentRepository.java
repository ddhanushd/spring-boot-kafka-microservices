package com.kafka.paymentservice.repository;

import com.kafka.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByEventId(String eventId);
    Optional<Payment> findByEventId(String eventId);
}
