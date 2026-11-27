
package com.kafka.paymentservice.controller;

import com.kafka.paymentservice.dto.PaymentRequest;
import com.kafka.paymentservice.entity.Payment;
import com.kafka.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment processPayment(@Valid @RequestBody PaymentRequest request) {

        Payment payment = new Payment();
        payment.setEventId(UUID.randomUUID().toString());
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());

        return paymentService.processPayment(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {

        Payment payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<Page<Payment>> getAllPayments(
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {

        Sort sort = Sort.by(
                pageable.getSort().stream()
                        .map(order -> order.getProperty().equals("createdAt")
                                ? order.nullsLast()
                                : order)
                        .toList()
        );

        Pageable updatedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Payment> payments =
                paymentService.getAllPayments(updatedPageable);

        return ResponseEntity.ok(payments);
    }
}
