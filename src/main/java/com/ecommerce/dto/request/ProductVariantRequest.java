package com.ecommerce.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ProductVariantRequest {
    private Long id;
    @NotBlank
    private String sku;
    @NotBlank
    private String size;
    @NotBlank
    private String color;
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime discountStartAt;
    private LocalDateTime discountEndAt;
    
    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;
}
