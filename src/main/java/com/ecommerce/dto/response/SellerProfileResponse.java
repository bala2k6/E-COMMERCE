package com.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfileResponse {
    private UserResponse seller;
    private List<ProductResponse> products;
    private List<OrderResponse> orders;
    private List<UserResponse> customers;
}
