package com.ecommerce.mapper;
import com.ecommerce.dto.response.ProductImageResponse;
import com.ecommerce.models.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductImageMapper {
    ProductImageResponse toResponse(ProductImage image);
    List<ProductImageResponse> toResponseList(List<ProductImage> images);
}
