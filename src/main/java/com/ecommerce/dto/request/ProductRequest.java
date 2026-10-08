package com.ecommerce.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @PositiveOrZero
    private BigDecimal basePrice;
    
    private String imageUrl;
    
    @NotNull
    private Long categoryId;
    
    private java.util.List<ProductVariantRequest> variants;
}
