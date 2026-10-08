package com.ecommerce.services;
import com.ecommerce.dto.request.AddressRequest;
import com.ecommerce.dto.response.AddressResponse;
import java.util.List;
public interface AddressService {
    AddressResponse addAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long id, AddressRequest request);
    AddressResponse getAddress(Long id);
    void deleteAddress(Long id);
    List<AddressResponse> getAddressesByUser(Long userId);
}
