package com.ecommerce.services.impl;

import com.ecommerce.services.UserService;

import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import com.ecommerce.repositories.AddressRepository;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.repositories.OrderRepository;
import com.ecommerce.dto.response.SellerProfileResponse;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    public User updateProfile(Long userId, com.ecommerce.dto.UpdateProfileRequest request) {
        User user = getUserProfile(userId);
        if (request.getName() != null) user.setName(request.getName());
        if (request.getMobileNumber() != null) user.setMobileNumber(request.getMobileNumber());
        
        if ("ROLE_CUSTOMER".equals(user.getRole().name())) {
            if (user.getCustomerDetails() == null) {
                user.setCustomerDetails(com.ecommerce.models.CustomerDetails.builder().user(user).build());
            }
            if (request.getDateOfBirth() != null) user.getCustomerDetails().setDateOfBirth(request.getDateOfBirth());
            if (request.getGender() != null) user.getCustomerDetails().setGender(request.getGender());
        }
        
        if ("ROLE_SELLER".equals(user.getRole().name())) {
            if (user.getSellerDetails() == null) {
                user.setSellerDetails(com.ecommerce.models.SellerDetails.builder().user(user).build());
            }
            if (request.getBusinessName() != null) user.getSellerDetails().setBusinessName(request.getBusinessName());
            if (request.getShopDescription() != null) user.getSellerDetails().setShopDescription(request.getShopDescription());
        }
        
        return userRepository.save(user);
    }

    public Address addAddress(Long userId, Address address) {
        User user = getUserProfile(userId);
        address.setUser(user);
        return addressRepository.save(address);
    }
    
    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateUserRole(Long userId, String roleStr) {
        User user = getUserProfile(userId);
        try {
            com.ecommerce.enums.Role role = com.ecommerce.enums.Role.valueOf(roleStr);
            user.setRole(role);
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role provided");
        }
    }

    @Override
    public SellerProfileResponse getSellerProfile(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        if (!"ROLE_SELLER".equals(seller.getRole().name())) {
            throw new RuntimeException("User is not a seller");
        }
        
        List<ProductResponse> products = productRepository.findBySellerId(sellerId)
                .stream().map(productMapper::toResponse).toList();
                
        List<com.ecommerce.models.Order> orders = orderRepository.findDistinctByOrderItems_ProductVariant_Product_SellerId(sellerId);
        List<OrderResponse> orderResponses = orders.stream().map(orderMapper::toResponse).toList();
        
        List<UserResponse> customers = orders.stream()
                .map(com.ecommerce.models.Order::getUser)
                .distinct()
                .map(userMapper::toResponse)
                .toList();
                
        return SellerProfileResponse.builder()
                .seller(userMapper.toResponse(seller))
                .products(products)
                .orders(orderResponses)
                .customers(customers)
                .build();
    }

    @Override
    public com.ecommerce.dto.response.CustomerProfileResponse getCustomerProfile(Long customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (!"ROLE_CUSTOMER".equals(customer.getRole().name())) {
            throw new RuntimeException("User is not a customer");
        }
        
        List<com.ecommerce.models.Order> orders = orderRepository.findByUserId(customerId);
        List<OrderResponse> orderResponses = orders.stream().map(orderMapper::toResponse).toList();
        
        return com.ecommerce.dto.response.CustomerProfileResponse.builder()
                .customer(userMapper.toResponse(customer))
                .orders(orderResponses)
                .build();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User toggleUserStatus(Long userId) {
        User user = getUserProfile(userId);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    @Override
    public User addWalletFunds(Long userId, java.math.BigDecimal amount) {
        User user = getUserProfile(userId);
        if (!"ROLE_CUSTOMER".equals(user.getRole().name())) {
            throw new RuntimeException("Only customers have a wallet");
        }
        if (user.getCustomerDetails() == null) {
            user.setCustomerDetails(com.ecommerce.models.CustomerDetails.builder().user(user).build());
        }
        if (user.getCustomerDetails().getWalletBalance() == null) {
            user.getCustomerDetails().setWalletBalance(java.math.BigDecimal.ZERO);
        }
        user.getCustomerDetails().setWalletBalance(user.getCustomerDetails().getWalletBalance().add(amount));
        return userRepository.save(user);
    }
}
