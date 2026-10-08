package com.ecommerce.services;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import java.util.List;
public interface UserService {
    User getUserProfile(Long userId);
    User updateProfile(Long userId, com.ecommerce.dto.UpdateProfileRequest request);
    Address addAddress(Long userId, Address address);
    List<Address> getUserAddresses(Long userId);
    List<User> getAllUsers();
    User updateUserRole(Long userId, String role);
    com.ecommerce.dto.response.SellerProfileResponse getSellerProfile(Long sellerId);
    com.ecommerce.dto.response.CustomerProfileResponse getCustomerProfile(Long customerId);
    void deleteUser(Long userId);
    User toggleUserStatus(Long userId);
    User addWalletFunds(Long userId, java.math.BigDecimal amount);
}
