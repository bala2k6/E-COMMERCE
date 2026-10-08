package com.ecommerce.dto.request;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class PricingRequest {
    private BigDecimal price;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime discountStartAt;
    private LocalDateTime discountEndAt;
}
