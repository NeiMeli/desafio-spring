package com.bootcamp.springchallenge.service.impl.customer;

import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTOBuilder;
import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.entity.customer.Province;
import com.bootcamp.springchallenge.repository.CustomerRepository;
import com.bootcamp.springchallenge.service.CustomerService;
import com.bootcamp.springchallenge.service.impl.customer.exception.CustomerServiceErrorImpl;
import com.bootcamp.springchallenge.service.impl.customer.exception.CustomerServiceException;
import com.bootcamp.springchallenge.service.impl.customer.query.CustomerQuery;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepository repository;

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO customerDTO) {
        validate(customerDTO);
        String userName = customerDTO.getUserName();
        Province province = resolveProvince(customerDTO.getProvince());
        Optional<Customer> existingCustomerOpt = repository.find(userName);
        if (existingCustomerOpt.isPresent()) {
            throw new CustomerServiceException(CustomerServiceErrorImpl.CUSTOMER_ALREADY_EXISTS);
        }
        Customer customer = repository.persist(new Customer(userName, province));
        return new CustomerResponseDTOBuilder(customer).build();
    }

    private Province resolveProvince(@Nullable String province) {
        return !Strings.isBlank(province) ? Province.fromLabel(province) : Province.defaultProvince();
    }

    private void validate(CustomerRequestDTO customerDTO) {
        if (Strings.isBlank(customerDTO.getUserName()))
            throw new CustomerServiceException(CustomerServiceErrorImpl.EMPTY_USERNAME);
    }

    @Override
    public List<CustomerResponseDTO> query(@NotNull final CustomerQuery query) {
        return repository.listWhere(query.buildPredicate())
                .sorted(query.getComparator())
                .map(c -> new CustomerResponseDTOBuilder(c).build())
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO update(CustomerRequestDTO customerDTO) {
        validate(customerDTO);
        String userName = customerDTO.getUserName();
        Optional<Customer> existingCustomerOpt = repository.find(userName);
        if (existingCustomerOpt.isEmpty()) {
            throw new CustomerServiceException(CustomerServiceErrorImpl.CUSTOMER_NOT_FOUND);
        }
        Customer customer = existingCustomerOpt.get();
        Province province = resolveProvince(customerDTO.getProvince());
        customer.setProvince(province);
        repository.persist(customer);
        return new CustomerResponseDTOBuilder(customer).build();
    }
}
