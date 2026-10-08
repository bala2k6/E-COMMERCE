package com.ecommerce.repositories;
import com.ecommerce.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findBySellerId(Long sellerId);
}
