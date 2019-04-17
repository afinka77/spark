package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.domain.Payment;
import com.transfers.repository.PaymentRepository;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

public class PaymentService {
    @Inject
    private PaymentRepository paymentRepository;

    @Transactional
    public List<Payment> getPayments() {
        return paymentRepository.getPayments();
    }
}
