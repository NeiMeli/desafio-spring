package com.bootcamp.springchallenge.service;

import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;
import com.bootcamp.springchallenge.service.impl.customer.query.CustomerQuery;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO create(CustomerRequestDTO customer);

    List<CustomerResponseDTO> query(@NotNull CustomerQuery query);

    CustomerResponseDTO update(CustomerRequestDTO customer);
}
