package com.ecommerce.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
}
