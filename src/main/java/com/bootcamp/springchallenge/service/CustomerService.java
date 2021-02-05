package com.bootcamp.springchallenge.service;

import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO create(CustomerRequestDTO customer);

    List<CustomerResponseDTO> listAll();
}
