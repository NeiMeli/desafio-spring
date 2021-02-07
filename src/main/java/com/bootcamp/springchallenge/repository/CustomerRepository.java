package com.bootcamp.springchallenge.repository;

import com.bootcamp.springchallenge.entity.customer.Customer;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface CustomerRepository {
    Optional<Customer> find(String userName);

    Customer persist(Customer customer);

    List<Customer> listAll();

    Stream<Customer> listWhere(Predicate<Customer> predicate);
}
