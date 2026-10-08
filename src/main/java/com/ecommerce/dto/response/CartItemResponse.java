package com.ecommerce.dto.response;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private Long variantId;
    private String variantName;
    private String size;
    private String color;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal mrp;
    private Integer quantity;
    private BigDecimal subtotal;
}
