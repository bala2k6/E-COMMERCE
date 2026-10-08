package com.ecommerce.services;
import com.ecommerce.dto.request.PaymentRequest;
import com.ecommerce.dto.response.PaymentResponse;
public interface PaymentService {
    PaymentResponse makePayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse getPaymentByOrder(Long orderId);
}
