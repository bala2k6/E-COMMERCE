package com.ecommerce.controllers;
import com.ecommerce.dto.request.CouponRequest;
import com.ecommerce.dto.response.CouponResponse;
import com.ecommerce.services.impl.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ecommerce.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<CouponResponse> createCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request, userDetails.getId()));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<CouponResponse> updateCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request, userDetails.getId()));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Void> deactivateCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        couponService.deactivateCoupon(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<List<CouponResponse>> getAdminCoupons(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(couponService.getCouponsBySeller(userDetails.getId()));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }
}
