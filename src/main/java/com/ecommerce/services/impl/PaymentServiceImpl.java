package com.ecommerce.services.impl;
import com.ecommerce.dto.request.PaymentRequest;
import com.ecommerce.dto.response.PaymentResponse;
import com.ecommerce.models.Order;
import com.ecommerce.models.Payment;
import com.ecommerce.enums.PaymentStatus;
import com.ecommerce.repositories.OrderRepository;
import com.ecommerce.repositories.PaymentRepository;
import com.ecommerce.mapper.PaymentMapper;
import com.ecommerce.services.PaymentService;
import com.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import com.ecommerce.repositories.UserRepository;
import com.ecommerce.models.CustomerDetails;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final UserRepository userRepository;
    
    @Override
    public PaymentResponse makePayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(order.getUser().getId());
        
        if ("WALLET".equalsIgnoreCase(request.getPaymentMethod())) {
            CustomerDetails cd = order.getUser().getCustomerDetails();
            if (cd == null || cd.getWalletBalance() == null || cd.getWalletBalance().compareTo(order.getTotalAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient wallet balance");
            }
            cd.setWalletBalance(cd.getWalletBalance().subtract(order.getTotalAmount()));
            userRepository.save(order.getUser());
        }
        
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();
        Payment saved = paymentRepository.save(payment);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        orderRepository.save(order);
        return paymentMapper.toResponse(saved);
    }
    
    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(payment.getOrder().getUser().getId());
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    public PaymentResponse getPaymentByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(payment.getOrder().getUser().getId());
        return paymentMapper.toResponse(payment);
    }
}
