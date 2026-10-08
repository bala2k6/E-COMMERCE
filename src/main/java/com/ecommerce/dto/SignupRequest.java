package com.ecommerce.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;

@Data
public class SignupRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", 
        message = "Password must be at least 8 characters long, contain at least one digit, one lowercase, one uppercase letter and one special character"
    )
    private String password;
    
    @Past(message = "Date of birth cannot be a future date")
    private java.time.LocalDate dateOfBirth;
    
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;
    
    private String gender;
}
