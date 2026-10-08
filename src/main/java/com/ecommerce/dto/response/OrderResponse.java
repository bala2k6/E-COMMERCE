package com.ecommerce.dto.response;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private AddressResponse shippingAddress;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime createdAt;
    private String couponCode;
    private BigDecimal discountAmount;
    private String userName;
    private List<OrderItemResponse> orderItems;
}
