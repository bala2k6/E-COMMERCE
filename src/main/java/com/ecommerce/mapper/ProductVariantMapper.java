package com.ecommerce.mapper;
import com.ecommerce.dto.request.ProductVariantRequest;
import com.ecommerce.dto.response.ProductVariantResponse;
import com.ecommerce.models.ProductVariant;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductVariantMapper {
    ProductVariant toEntity(ProductVariantRequest request);
    ProductVariantResponse toResponse(ProductVariant variant);
    List<ProductVariantResponse> toResponseList(List<ProductVariant> variants);
    List<ProductVariant> toEntityList(List<ProductVariantRequest> requests);

    @AfterMapping
    default void calculateDiscounts(ProductVariant variant, @MappingTarget ProductVariantResponse response) {
        if (variant.getMrp() != null && variant.getSellingPrice() != null) {
            BigDecimal mrp = variant.getMrp();
            BigDecimal sellingPrice = variant.getSellingPrice();
            
            // If sellingPrice > mrp, no discount
            if (mrp.compareTo(sellingPrice) > 0) {
                BigDecimal savings = mrp.subtract(sellingPrice);
                response.setSavingsAmount(savings);
                
                if (mrp.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentage = savings.multiply(new BigDecimal("100")).divide(mrp, 2, RoundingMode.HALF_UP);
                    response.setDiscountPercentage(percentage);
                }
            }
        }
    }
}
