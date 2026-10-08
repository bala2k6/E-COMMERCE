package com.ecommerce.services;

import com.ecommerce.dto.CartRequest;
import com.ecommerce.models.Cart;

public interface CartService {
    Cart getCart(Long userId);
    Cart addToCart(Long userId, CartRequest request);
    Cart removeCartItem(Long userId, Long variantId);
    Cart updateCartItemQuantity(Long userId, Long variantId, int quantity);
    void clearCart(Long userId);
}
