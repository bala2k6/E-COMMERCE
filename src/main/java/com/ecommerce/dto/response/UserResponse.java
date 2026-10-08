package com.ecommerce.dto.response;
import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDate dateOfBirth;
    private String mobileNumber;
    private String gender;
    
    // Seller fields
    private String businessName;
    private String shopDescription;
    
    private BigDecimal walletBalance;
    
    private boolean active;
}
