package com.ecommerce.dto.response;
import lombok.Data;
@Data
public class ProductImageResponse {
    private Long id;
    private String url;
    private int sortOrder;
    private boolean isPrimary;
}
