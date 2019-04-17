package com.transfers.api;

import com.google.inject.Inject;
import com.transfers.domain.Payment;
import com.transfers.service.CustomerService;
import com.transfers.service.PaymentService;
import spark.Service;

public class PaymentController extends BaseController<Payment> {
    @Inject
    private PaymentService paymentService;

    @Override
    public void configure(Service spark) {
        spark.get("/payments", (req, res) -> {
            return dataToJson(paymentService.getPayments());
        });
    }
}
