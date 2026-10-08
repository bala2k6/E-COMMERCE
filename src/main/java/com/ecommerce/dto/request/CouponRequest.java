package com.ecommerce.dto.request;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class CouponRequest {
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
}
