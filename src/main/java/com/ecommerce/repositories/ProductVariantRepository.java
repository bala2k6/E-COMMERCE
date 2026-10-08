package com.ecommerce.repositories;
import com.ecommerce.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {}
