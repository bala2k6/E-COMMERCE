package com.ecommerce.config;

import com.ecommerce.enums.Role;
import com.ecommerce.models.*;
import com.ecommerce.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final ReviewRepository reviewRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Using existing data from database.");
        
        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role VARCHAR(255)");
            System.out.println("Modified role column to VARCHAR(255) to prevent ENUM truncation.");
        } catch (Exception e) {
            System.out.println("Could not modify role column, it might already be correct: " + e.getMessage());
        }
        
        if (!userRepository.existsByEmail("admin@shopkart.com")) {
            User admin = User.builder()
                .name("Super Admin")
                .email("admin@shopkart.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .build();
            userRepository.save(admin);
            System.out.println("Default Admin seeded.");
        }
    }
}
