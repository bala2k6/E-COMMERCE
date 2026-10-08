package com.ecommerce.dto.response;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
public class CartResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;
}
