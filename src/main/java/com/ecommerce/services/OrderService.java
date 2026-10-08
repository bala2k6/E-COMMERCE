package com.ecommerce.services;
import com.ecommerce.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order placeOrder(Long userId, Long addressId, String couponCode);
    Order getOrder(Long orderId);
    java.util.List<Order> getUserOrders(Long userId);
    Page<Order> getAllOrders(Pageable pageable);
    Page<com.ecommerce.dto.response.OrderResponse> getOrdersForAdmin(Pageable pageable, Long sellerId);
    Order updateOrderStatus(Long orderId, String status);
    Order cancelOrder(Long orderId);
}
