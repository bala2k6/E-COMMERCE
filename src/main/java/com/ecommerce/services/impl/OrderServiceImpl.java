package com.ecommerce.services.impl;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.PaymentStatus;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.CartService;
import com.ecommerce.models.*;
import com.ecommerce.repositories.*;
import com.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final com.ecommerce.mapper.OrderMapper orderMapper;

    @org.springframework.transaction.annotation.Transactional
    public Order placeOrder(Long userId, Long addressId, String couponCode) {
        Cart cart = cartService.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        SecurityUtils.assertOwnerOrAdmin(address.getUser().getId());

        Order order = Order.builder()
                .user(cart.getUser())
                .shippingAddress(address)
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PLACED)
                .createdAt(LocalDateTime.now())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            BigDecimal itemPrice = ci.getProductVariant().getSellingPrice() != null ? ci.getProductVariant().getSellingPrice() : ci.getProductVariant().getPrice();
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .productVariant(ci.getProductVariant())
                    .quantity(ci.getQuantity())
                    .price(itemPrice)
                    .build();
            order.getOrderItems().add(oi);
            
            if(itemPrice != null) {
                total = total.add(itemPrice.multiply(new BigDecimal(ci.getQuantity())));
            }
        }
        
        // Apply Coupon
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode)
                    .orElseThrow(() -> new RuntimeException("Invalid or inactive coupon"));
            
            LocalDateTime now = LocalDateTime.now();
            if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
                throw new RuntimeException("Coupon is not yet valid");
            }
            if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
                throw new RuntimeException("Coupon has expired");
            }
            
            if (coupon.getDiscountPercentage() != null) {
                discount = total.multiply(coupon.getDiscountPercentage()).divide(new BigDecimal("100"));
                if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                    discount = coupon.getMaxDiscountAmount();
                }
            }
            order.setCoupon(coupon);
            order.setDiscountAmount(discount);
        }
        
        order.setTotalAmount(total.subtract(discount));
        
        OrderTracking tracking = OrderTracking.builder()
                .order(order)
                .status(OrderStatus.PLACED)
                .timestamp(LocalDateTime.now())
                .description("Order Placed")
                .build();
        
        order.getTrackingHistory().add(tracking);

        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);
        
        return savedOrder;
    }

    public Order getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isOwner = currentUserId != null && currentUserId.equals(order.getUser().getId());
        boolean isAdmin = SecurityUtils.isAdmin();
        boolean isSeller = false;
        
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.ecommerce.security.UserDetailsImpl) {
            isSeller = ((com.ecommerce.security.UserDetailsImpl) auth.getPrincipal()).getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"));
        }

        if (!isOwner && !isAdmin && !isSeller) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied: You don't have permission to access this resource");
        }
        return order;
    }

    public List<Order> getUserOrders(Long userId) {
        SecurityUtils.assertOwnerOrAdmin(userId);
        return orderRepository.findByUserId(userId);
    }
    
    public org.springframework.data.domain.Page<Order> getAllOrders(org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    public org.springframework.data.domain.Page<com.ecommerce.dto.response.OrderResponse> getOrdersForAdmin(org.springframework.data.domain.Pageable pageable, Long sellerId) {
        return orderRepository.findDistinctByOrderItems_ProductVariant_Product_SellerId(sellerId, pageable)
            .map(order -> {
                com.ecommerce.dto.response.OrderResponse response = orderMapper.toResponse(order);
                // Filter order items
                response.setOrderItems(response.getOrderItems().stream()
                    .filter(item -> {
                        com.ecommerce.models.Product product = order.getOrderItems().stream()
                            .filter(oi -> oi.getProductVariant().getProduct().getId().equals(item.getProductId()))
                            .map(oi -> oi.getProductVariant().getProduct())
                            .findFirst().orElse(null);
                        return product != null && product.getSeller() != null && product.getSeller().getId().equals(sellerId);
                    }).toList());
                // Recalculate total amount
                java.math.BigDecimal sellerTotal = response.getOrderItems().stream()
                    .map(item -> item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                response.setTotalAmount(sellerTotal);
                return response;
            });
    }
    
    @org.springframework.transaction.annotation.Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrder(orderId);
        order.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }
    
    @org.springframework.transaction.annotation.Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order cannot be cancelled in its current state.");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        
        OrderTracking tracking = OrderTracking.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .timestamp(LocalDateTime.now())
                .description("Order Cancelled by User")
                .build();
        
        order.getTrackingHistory().add(tracking);
        return orderRepository.save(order);
    }
}
