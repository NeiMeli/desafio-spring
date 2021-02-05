package com.bootcamp.springchallenge.repository;

import com.bootcamp.springchallenge.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> find(String userName);

    Customer persist(Customer customer);

    List<Customer> listAll();
}
