package com.ecommerce.mapper;
import com.ecommerce.dto.response.OrderItemResponse;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AddressMapper.class})
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    OrderResponse toResponse(Order order);
    @Mapping(target = "productId", source = "productVariant.product.id")
    @Mapping(target = "productName", source = "productVariant.product.name")
    OrderItemResponse toItemResponse(OrderItem orderItem);
    List<OrderResponse> toResponseList(List<Order> orders);

    @AfterMapping
    default void mapCouponCode(Order order, @MappingTarget OrderResponse response) {
        if (order.getCoupon() != null) {
            response.setCouponCode(order.getCoupon().getCode());
        }
    }

    @AfterMapping
    default void mapItemImageUrl(OrderItem orderItem, @MappingTarget OrderItemResponse response) {
        if (orderItem.getProductVariant() != null
                && orderItem.getProductVariant().getProduct() != null
                && orderItem.getProductVariant().getProduct().getImageUrl() != null) {
            response.setImageUrl(orderItem.getProductVariant().getProduct().getImageUrl());
        }
    }
}
