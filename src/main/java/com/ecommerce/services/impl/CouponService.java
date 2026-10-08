package com.ecommerce.services.impl;
import com.ecommerce.dto.request.CouponRequest;
import com.ecommerce.dto.response.CouponResponse;
import com.ecommerce.mapper.CouponMapper;
import com.ecommerce.models.Coupon;
import com.ecommerce.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final com.ecommerce.repositories.UserRepository userRepository;
    
    public CouponResponse createCoupon(CouponRequest request, Long sellerId) {
        com.ecommerce.models.User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        Coupon coupon = couponMapper.toEntity(request);
        coupon.setSeller(seller);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }
    
    public CouponResponse updateCoupon(Long id, CouponRequest request, Long sellerId) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new RuntimeException("Coupon not found"));
        if (coupon.getSeller() != null && !coupon.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this coupon");
        }
        coupon.setCode(request.getCode());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setActive(request.isActive());
        return couponMapper.toResponse(couponRepository.save(coupon));
    }
    
    public void deactivateCoupon(Long id, Long sellerId) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new RuntimeException("Coupon not found"));
        if (coupon.getSeller() != null && !coupon.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this coupon");
        }
        coupon.setActive(false);
        couponRepository.save(coupon);
    }
    
    public List<CouponResponse> getAllCoupons() {
        return couponMapper.toResponseList(couponRepository.findAll());
    }

    public List<CouponResponse> getCouponsBySeller(Long sellerId) {
        return couponMapper.toResponseList(couponRepository.findBySellerId(sellerId));
    }
    
    public List<CouponResponse> getActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponMapper.toResponseList(couponRepository.findAll().stream()
            .filter(c -> c.isActive() 
                && (c.getValidFrom() == null || !now.isBefore(c.getValidFrom()))
                && (c.getValidUntil() == null || !now.isAfter(c.getValidUntil())))
            .toList());
    }
}
