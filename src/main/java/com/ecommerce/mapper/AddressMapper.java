package com.ecommerce.mapper;
import com.ecommerce.dto.request.AddressRequest;
import com.ecommerce.dto.response.AddressResponse;
import com.ecommerce.models.Address;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    Address toEntity(AddressRequest request);
    AddressResponse toResponse(Address address);
    List<AddressResponse> toResponseList(List<Address> addresses);
}
