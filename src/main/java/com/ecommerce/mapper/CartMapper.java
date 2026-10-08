package com.ecommerce.mapper;
import com.ecommerce.dto.response.CartItemResponse;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ProductMapper.class})
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartResponse toResponse(Cart cart);
    
    @Mapping(target = "variantId", source = "productVariant.id")
    @Mapping(target = "variantName", source = "productVariant.variantName")
    @Mapping(target = "size", source = "productVariant.size")
    @Mapping(target = "color", source = "productVariant.color")
    @Mapping(target = "imageUrl", source = "productVariant.imageUrl")
    @Mapping(target = "price", source = "productVariant.sellingPrice")
    @Mapping(target = "mrp", source = "productVariant.mrp")
    @Mapping(target = "product", source = "productVariant.product")
    CartItemResponse toItemResponse(CartItem cartItem);
}
