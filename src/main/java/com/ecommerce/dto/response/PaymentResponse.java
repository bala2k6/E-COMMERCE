package com.ecommerce.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private String status;
    private LocalDateTime paymentDate;
}
