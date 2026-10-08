package com.ecommerce.models;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Product product;
    
    @Column(nullable = false)
    private String url;
    
    private int sortOrder;
    private boolean isPrimary;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;

    private String contentType;
}
