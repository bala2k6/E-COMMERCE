package com.ecommerce.services.impl;
import com.ecommerce.dto.request.ReviewRequest;
import com.ecommerce.dto.response.ReviewResponse;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;
import com.ecommerce.models.User;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.repositories.ReviewRepository;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.mapper.ReviewMapper;
import com.ecommerce.services.ReviewService;
import com.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    @Override
    public ReviewResponse addReview(Long userId, ReviewRequest request) {
        SecurityUtils.assertOwnerOrAdmin(userId);
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(request.getProductId()).orElseThrow();
        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }
    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(review.getUser().getId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }
    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(review.getUser().getId());
        reviewRepository.deleteById(id);
    }
    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return reviewMapper.toResponseList(reviewRepository.findByProductId(productId));
    }
    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        SecurityUtils.assertOwnerOrAdmin(userId);
        return reviewMapper.toResponseList(reviewRepository.findByUserId(userId));
    }
}
