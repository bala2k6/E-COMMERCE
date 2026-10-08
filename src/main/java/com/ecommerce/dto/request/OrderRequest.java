package com.ecommerce.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class OrderRequest {
    @NotNull
    private Long addressId;
    private Long couponId;
}
