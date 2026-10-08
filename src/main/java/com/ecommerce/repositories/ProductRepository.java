package com.ecommerce.repositories;
import com.ecommerce.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.seller.active = true AND p.category.id = :categoryId")
    Page<Product> findByCategoryIdAndActiveTrue(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.seller.active = true")
    Page<Product> findByActiveTrue(Pageable pageable);
    
    List<Product> findBySellerId(Long sellerId);
    
    Page<Product> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.seller.active = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.active = true AND p.seller.active = true")
    List<Product> findAllActiveWithImages();
}
