package com.ecommerce.services.impl;
import com.ecommerce.dto.request.AddressRequest;
import com.ecommerce.dto.response.AddressResponse;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import com.ecommerce.repositories.AddressRepository;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.services.AddressService;
import com.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    @Override
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        SecurityUtils.assertOwnerOrAdmin(userId);
        User user = userRepository.findById(userId).orElseThrow();
        Address address = addressMapper.toEntity(request);
        address.setUser(user);
        return addressMapper.toResponse(addressRepository.save(address));
    }
    @Override
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        Address address = addressRepository.findById(id).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(address.getUser().getId());
        Address updated = addressMapper.toEntity(request);
        updated.setId(id);
        updated.setUser(address.getUser());
        return addressMapper.toResponse(addressRepository.save(updated));
    }
    @Override
    public AddressResponse getAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(address.getUser().getId());
        return addressMapper.toResponse(address);
    }
    @Override
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow();
        SecurityUtils.assertOwnerOrAdmin(address.getUser().getId());
        addressRepository.deleteById(id);
    }
    @Override
    public List<AddressResponse> getAddressesByUser(Long userId) {
        SecurityUtils.assertOwnerOrAdmin(userId);
        return addressMapper.toResponseList(addressRepository.findByUserId(userId));
    }
}
