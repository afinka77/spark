package com.transfers.api;

import com.google.inject.Inject;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Payment;
import com.transfers.service.PaymentService;
import spark.Service;

public class PaymentController extends BaseController<PaymentDto> {
    @Inject
    private PaymentService paymentService;

    @Override
    public void configure(Service spark) {
        spark.get("/customers/:customerId/payments", (req, res) -> {
            return dataToJson(paymentService.getPayments(req.params("customerId")));
        });
        spark.get("/customers/:customerId/payments/:paymentId", (req, res) -> {
            return dataToJson(paymentService.getPayment(req.params("customerId"), req.params("paymentId")));
        });
        spark.post("/customers/:customerId/payments", (req, res) -> {
            Payment payment = paymentService.createPayment(
                    req.params("customerId"), jsonToData(req.body(), PaymentDto.class));
            return dataToJson(payment);
        });

        spark.put("/customers/:customerId/payments/:paymentId", (req, res) -> {
            return dataToJson(paymentService.executePayment(req.params("customerId"), req.params("paymentId")));
        });
    }
}
