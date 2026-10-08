package com.ecommerce.repositories;
import com.ecommerce.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    
    Page<Order> findDistinctByOrderItems_ProductVariant_Product_SellerId(Long sellerId, Pageable pageable);
    
    List<Order> findDistinctByOrderItems_ProductVariant_Product_SellerId(Long sellerId);
}
