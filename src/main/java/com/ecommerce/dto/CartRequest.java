package com.ecommerce.dto;
import lombok.Data;
@Data
public class CartRequest {
    private Long productVariantId;
    private int quantity;
}
