$reposDir = "d:\E-Commerce\backend\src\main\java\com\ecommerce\repositories"
New-Item -ItemType Directory -Force -Path $reposDir | Out-Null

$repos = @(
    @{ Name = "UserRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.User;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport java.util.Optional;`npublic interface UserRepository extends JpaRepository<User, Long> {`n    Optional<User> findByEmail(String email);`n    boolean existsByEmail(String email);`n}" },
    @{ Name = "AddressRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Address;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport java.util.List;`npublic interface AddressRepository extends JpaRepository<Address, Long> {`n    List<Address> findByUserId(Long userId);`n}" },
    @{ Name = "CategoryRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Category;`nimport org.springframework.data.jpa.repository.JpaRepository;`npublic interface CategoryRepository extends JpaRepository<Category, Long> {}" },
    @{ Name = "ProductRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Product;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport org.springframework.data.jpa.repository.Query;`nimport org.springframework.data.repository.query.Param;`nimport java.util.List;`npublic interface ProductRepository extends JpaRepository<Product, Long> {`n    List<Product> findByCategoryId(Long categoryId);`n    @Query(`"SELECT p FROM Product p WHERE p.active = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))`")`n    List<Product> searchActiveProducts(@Param(`"keyword`") String keyword);`n}" },
    @{ Name = "ProductVariantRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.ProductVariant;`nimport org.springframework.data.jpa.repository.JpaRepository;`npublic interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {}" },
    @{ Name = "OrderRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Order;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport java.util.List;`npublic interface OrderRepository extends JpaRepository<Order, Long> {`n    List<Order> findByUserId(Long userId);`n}" },
    @{ Name = "CartRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Cart;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport java.util.Optional;`npublic interface CartRepository extends JpaRepository<Cart, Long> {`n    Optional<Cart> findByUserId(Long userId);`n}" },
    @{ Name = "CartItemRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.CartItem;`nimport org.springframework.data.jpa.repository.JpaRepository;`npublic interface CartItemRepository extends JpaRepository<CartItem, Long> {}" },
    @{ Name = "CouponRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Coupon;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport java.util.Optional;`npublic interface CouponRepository extends JpaRepository<Coupon, Long> {`n    Optional<Coupon> findByCodeAndActiveTrue(String code);`n}" },
    @{ Name = "ReviewRepository"; Content = "package com.ecommerce.repositories;`nimport com.ecommerce.models.Review;`nimport org.springframework.data.jpa.repository.JpaRepository;`nimport java.util.List;`npublic interface ReviewRepository extends JpaRepository<Review, Long> {`n    List<Review> findByProductId(Long productId);`n}" }
)

foreach ($repo in $repos) {
    $path = "$reposDir\$($repo.Name).java"
    Set-Content -Path $path -Value $repo.Content -Encoding UTF8
}
