package com.ecommerce.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String name;
    private String mobileNumber;
    
    // Customer specific fields
    private LocalDate dateOfBirth;
    private String gender;
    
    // Seller specific fields
    private String businessName;
    private String shopDescription;
}
