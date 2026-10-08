package com.ecommerce.dto.response;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ProductVariantResponse {
    private Long id;
    private String sku;
    private String size;
    private String color;
    private String variantName;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime discountStartAt;
    private LocalDateTime discountEndAt;
    private BigDecimal savingsAmount;
    private BigDecimal discountPercentage;
    
    private Integer stockQuantity;
}
