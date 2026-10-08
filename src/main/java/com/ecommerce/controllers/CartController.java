package com.ecommerce.controllers;
import com.ecommerce.dto.CartRequest;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
public class CartController {
    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.getCart(userDetails.getId())));
    }
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.addToCart(userDetails.getId(), request)));
    }

    @DeleteMapping("/items/{variantId}")
    public ResponseEntity<CartResponse> removeCartItem(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long variantId) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.removeCartItem(userDetails.getId(), variantId)));
    }

    @PutMapping("/items/{variantId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long variantId, @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.updateCartItemQuantity(userDetails.getId(), variantId, request.getQuantity())));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
