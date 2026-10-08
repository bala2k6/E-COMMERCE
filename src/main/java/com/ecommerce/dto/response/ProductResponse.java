package com.ecommerce.dto.response;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private boolean active;
    private String imageUrl;
    private CategoryResponse category;
    private java.util.List<ProductImageResponse> images;
    private java.util.List<ProductVariantResponse> variants;
}
