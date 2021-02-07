package com.bootcamp.springchallenge.controller.customer;

import com.bootcamp.springchallenge.controller.ExceptionHandlerController;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;
import com.bootcamp.springchallenge.service.CustomerService;
import com.bootcamp.springchallenge.service.impl.customer.query.CustomerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController extends ExceptionHandlerController {

    @Autowired
    CustomerService service;

    @PostMapping()
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody CustomerRequestDTO customer) {
        return new ResponseEntity<>(service.create(customer), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<CustomerResponseDTO>> query(@RequestParam (required = false) String province) {
        CustomerQuery customerQuery = new CustomerQuery().withProvince(province);
        return new ResponseEntity<>(service.query(customerQuery), HttpStatus.OK);
    }
}
