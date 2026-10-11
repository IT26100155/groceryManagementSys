package com.grocerymanagementsys.address.services;

import com.grocerymanagementsys.address.dtos.AddressRequestDto;
import com.grocerymanagementsys.address.dtos.AddressResponseDto;
import com.grocerymanagementsys.address.entities.Address;
import com.grocerymanagementsys.address.exceptions.AddressInUseException;
import com.grocerymanagementsys.address.repositories.AddressRepository;
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.customer.repositories.CustomerRepository;
import com.grocerymanagementsys.order.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public AddressResponseDto create(Integer customerId, AddressRequestDto dto) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", customerId);
        }

        Address address = new Address();
        address.setCustomerId(customerId);
        address.setAddressBody(dto.addressBody());
        return toResponse(addressRepository.save(address));
    }

    public List<AddressResponseDto> getAll(Integer customerId) {
        return addressRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    public AddressResponseDto update(Integer customerId, Integer addressId, AddressRequestDto dto) {
        Address address = findOwnedAddressOrThrow(customerId, addressId);
        address.setAddressBody(dto.addressBody());
        return toResponse(addressRepository.save(address));
    }

    public void delete(Integer customerId, Integer addressId) {
        Address address = findOwnedAddressOrThrow(customerId, addressId);
        if (orderRepository.existsByAddressId(addressId)) {
            throw new AddressInUseException(addressId);
        }
        addressRepository.delete(address);
    }

    private Address findOwnedAddressOrThrow(Integer customerId, Integer addressId) {
        return addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId));
    }

    private AddressResponseDto toResponse(Address address) {
        return new AddressResponseDto(address.getId(), address.getCustomerId(), address.getAddressBody());
    }
}
