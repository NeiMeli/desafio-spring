package com.bootcamp.springchallenge.service.impl.customer;

import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTOBuilder;
import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.repository.CustomerRepository;
import com.bootcamp.springchallenge.service.CustomerService;
import com.bootcamp.springchallenge.service.impl.customer.exception.CustomerServiceErrorImpl;
import com.bootcamp.springchallenge.service.impl.customer.exception.CustomerServiceException;
import com.bootcamp.springchallenge.service.impl.customer.query.CustomerQuery;
import org.jetbrains.annotations.NotNull;
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
        String userName = customerDTO.getUserName();
        Optional<Customer> existingCustomerOpt = repository.find(userName);
        if (existingCustomerOpt.isPresent()) {
            throw new CustomerServiceException(CustomerServiceErrorImpl.CUSTOMER_ALREADY_EXISTS);
        }
        Customer customer = repository.persist(new Customer(userName));
        return new CustomerResponseDTOBuilder(customer).build();
    }

    @Override
    public List<CustomerResponseDTO> listAll() {
        return repository.listAll().stream().map(c -> new CustomerResponseDTOBuilder(c).build()).collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponseDTO> query(@NotNull final CustomerQuery query) {
        return repository.listWhere(query.buildPredicate())
                .sorted(query.getComparator())
                .map(c -> new CustomerResponseDTOBuilder(c).build())
                .collect(Collectors.toList());
    }
}
