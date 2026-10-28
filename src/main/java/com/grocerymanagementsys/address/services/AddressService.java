package com.grocerymanagementsys.address.services;

import com.grocerymanagementsys.address.dtos.AddressRequestDto;
import com.grocerymanagementsys.address.dtos.AddressResponseDto;
import com.grocerymanagementsys.address.entities.Address;
import com.grocerymanagementsys.address.exceptions.AddressInUseException;
import com.grocerymanagementsys.address.repositories.AddressRepository;
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.customer.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final CustomerService customerService;

    @Transactional
    public AddressResponseDto create(Integer customerId, AddressRequestDto dto) {
        Customer customer = customerService.findCustomerOrThrow(customerId);

        Address address = new Address();
        address.setCustomer(customer);
        address.setAddressBody(dto.addressBody());
        return toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAll(Integer customerId) {
        return customerService.findCustomerOrThrow(customerId).getAddresses().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponseDto update(Integer customerId, Integer addressId, AddressRequestDto dto) {
        Address address = findOwnedAddressOrThrow(customerService.findCustomerOrThrow(customerId), addressId);
        address.setAddressBody(dto.addressBody());
        return toResponse(address);
    }

    @Transactional
    public void delete(Integer customerId, Integer addressId) {
        Customer customer = customerService.findCustomerOrThrow(customerId);
        Address address = findOwnedAddressOrThrow(customer, addressId);

        if (!address.getOrders().isEmpty()) {
            throw new AddressInUseException(addressId);
        }
        customer.getAddresses().remove(address);
    }

    private Address findOwnedAddressOrThrow(Customer customer, Integer addressId) {
        return customer.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId));
    }

    private AddressResponseDto toResponse(Address address) {
        return new AddressResponseDto(address.getId(), address.getCustomer().getId(), address.getAddressBody());
    }
}
