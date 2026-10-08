package com.ecommerce.controllers;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.services.UserService;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userMapper.toResponse(userService.getUserProfile(userDetails.getId())));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody com.ecommerce.dto.UpdateProfileRequest request) {
        return ResponseEntity.ok(userMapper.toResponse(userService.updateProfile(userDetails.getId(), request)));
    }
    @PostMapping("/addresses")
    public ResponseEntity<Address> addAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Address address) {
        return ResponseEntity.ok(userService.addAddress(userDetails.getId(), address));
    }
    @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getUserAddresses(userDetails.getId()));
    }
    
    @GetMapping("/admin/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(userMapper::toResponse).toList());
    }
    
    @PutMapping("/admin/{userId}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userMapper.toResponse(userService.updateUserRole(userId, role)));
    }
    
    @GetMapping("/admin/sellers/{sellerId}/profile")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.ecommerce.dto.response.SellerProfileResponse> getSellerProfile(@PathVariable Long sellerId) {
        return ResponseEntity.ok(userService.getSellerProfile(sellerId));
    }

    @GetMapping("/admin/customers/{customerId}/profile")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.ecommerce.dto.response.CustomerProfileResponse> getCustomerProfile(@PathVariable Long customerId) {
        return ResponseEntity.ok(userService.getCustomerProfile(customerId));
    }

    @DeleteMapping("/admin/{userId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{userId}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(userMapper.toResponse(userService.toggleUserStatus(userId)));
    }

    @PostMapping("/wallet/add")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserResponse> addWalletFunds(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam java.math.BigDecimal amount) {
        return ResponseEntity.ok(userMapper.toResponse(userService.addWalletFunds(userDetails.getId(), amount)));
    }
}
