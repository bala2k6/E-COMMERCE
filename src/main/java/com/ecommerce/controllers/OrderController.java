package com.ecommerce.controllers;
import com.ecommerce.models.Order;
import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    
    @PostMapping("/place/{addressId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long addressId, @RequestParam(required = false) String couponCode) {
        Order order = orderService.placeOrder(userDetails.getId(), addressId, couponCode);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.getOrder(orderId)));
    }
    
    @GetMapping("/my-orders")
    public ResponseEntity<java.util.List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        java.util.List<OrderResponse> response = orderService.getUserOrders(userDetails.getId())
            .stream()
            .map(orderMapper::toResponse)
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<org.springframework.data.domain.Page<OrderResponse>> getAllOrders(org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable).map(orderMapper::toResponse));
    }
    
    @GetMapping("/admin/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<org.springframework.data.domain.Page<OrderResponse>> getAdminOrders(@AuthenticationPrincipal UserDetailsImpl userDetails, org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersForAdmin(pageable, userDetails.getId()));
    }

    @PutMapping("/{orderId}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateOrderStatus(orderId, status)));
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.cancelOrder(orderId)));
    }
}
