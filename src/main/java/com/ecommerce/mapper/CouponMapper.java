package com.ecommerce.mapper;
import com.ecommerce.dto.request.CouponRequest;
import com.ecommerce.dto.response.CouponResponse;
import com.ecommerce.models.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CouponMapper {
    Coupon toEntity(CouponRequest request);
    CouponResponse toResponse(Coupon coupon);
    List<CouponResponse> toResponseList(List<Coupon> coupons);
}
