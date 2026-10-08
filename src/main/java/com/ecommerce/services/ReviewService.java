package com.ecommerce.services;
import com.ecommerce.dto.request.ReviewRequest;
import com.ecommerce.dto.response.ReviewResponse;
import java.util.List;
public interface ReviewService {
    ReviewResponse addReview(Long userId, ReviewRequest request);
    ReviewResponse updateReview(Long id, ReviewRequest request);
    void deleteReview(Long id);
    List<ReviewResponse> getReviewsByProduct(Long productId);
    List<ReviewResponse> getReviewsByUser(Long userId);
}
