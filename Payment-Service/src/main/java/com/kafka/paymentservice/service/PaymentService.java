package com.kafka.paymentservice.service;

import com.kafka.paymentservice.entity.Payment;
import com.kafka.paymentservice.exception.PaymentNotFoundException;
import com.kafka.paymentservice.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Payment payment) {

        return paymentRepository.findByEventId(payment.getEventId())
                .orElseGet(() -> {
                    payment.setStatus("SUCCESS");
                    return paymentRepository.save(payment);
                });
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found with ID: " + id
                        )
                );
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
}
