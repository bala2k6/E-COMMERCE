package com.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Product product;

    private String sku;
    private String size;
    private String color;
    private String variantName; // e.g., "Size M / Red"
    private String imageUrl; // Optional variant-specific image
    
    // Total price or delta. Let's use absolute price for the variant
    private BigDecimal price;
    
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    
    private BigDecimal discountValue;
    
    private LocalDateTime discountStartAt;
    private LocalDateTime discountEndAt;
    
    private int stockQuantity;
    
    public enum DiscountType {
        PERCENTAGE, FLAT
    }
}
