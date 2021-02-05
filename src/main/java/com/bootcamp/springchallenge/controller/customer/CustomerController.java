package com.bootcamp.springchallenge.controller.customer;

import com.bootcamp.springchallenge.controller.BadRequestHandlerController;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;
import com.bootcamp.springchallenge.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController extends BadRequestHandlerController {

    @Autowired
    CustomerService service;

    @PostMapping("/customer")
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody CustomerRequestDTO customer) {
        return new ResponseEntity<>(service.create(customer), HttpStatus.OK);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<CustomerResponseDTO>> listAllCustomers() {
        return new ResponseEntity<>(service.listAll(), HttpStatus.OK);
    }
}
