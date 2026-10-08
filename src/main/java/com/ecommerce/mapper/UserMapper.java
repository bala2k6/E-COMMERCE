package com.ecommerce.mapper;
import com.ecommerce.dto.SignupRequest;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "name", source = "name")
    User toEntity(SignupRequest request);
    @Mapping(target = "dateOfBirth", source = "customerDetails.dateOfBirth")
    @Mapping(target = "gender", source = "customerDetails.gender")
    @Mapping(target = "businessName", source = "sellerDetails.businessName")
    @Mapping(target = "shopDescription", source = "sellerDetails.shopDescription")
    @Mapping(target = "walletBalance", source = "customerDetails.walletBalance")
    UserResponse toResponse(User user);
}
