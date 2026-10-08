package com.ecommerce.services.impl;

import com.ecommerce.services.CartService;

import com.ecommerce.dto.CartRequest;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.ProductVariant;
import com.ecommerce.models.User;
import com.ecommerce.repositories.CartItemRepository;
import com.ecommerce.repositories.CartRepository;
import com.ecommerce.repositories.ProductVariantRepository;
import com.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public Cart getCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow();
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    public Cart addToCart(Long userId, CartRequest request) {
        Cart cart = getCart(userId);
        ProductVariant pv = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Product Variant not found"));
                
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(pv.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(pv)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }
        return cartRepository.save(cart);
    }

    public Cart removeCartItem(Long userId, Long variantId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> {
            if (item.getProductVariant().getId().equals(variantId)) {
                cartItemRepository.delete(item);
                return true;
            }
            return false;
        });
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(Long userId, Long variantId, int quantity) {
        Cart cart = getCart(userId);
        cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(variantId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(Math.max(1, quantity));
                    cartItemRepository.save(item);
                });
        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
