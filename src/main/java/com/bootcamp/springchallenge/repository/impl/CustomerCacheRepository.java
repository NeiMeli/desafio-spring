package com.bootcamp.springchallenge.repository.impl;

import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.exception.BadRequestException;
import com.bootcamp.springchallenge.repository.CacheDBTable;
import com.bootcamp.springchallenge.repository.CacheRepository;
import com.bootcamp.springchallenge.repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Repository
public class CustomerCacheRepository implements CustomerRepository, CacheRepository<String, Customer> {
    final CacheDBTable<String, Customer> database;

    public CustomerCacheRepository() throws Exception {
        database = new CacheDBTable<>() {
            @Override
            protected @NotNull String generateNextId() {
                throw new BadRequestException("No se puede guardar un cliente sin nombre!");
            }
        };
        JsonNode jsonNodes = JsonDBUtil.parseDatabase("src/main/resources/database/customers.json");
        for (JsonNode jsonNode : jsonNodes) {
            database.persist(Customer.fromJson(jsonNode));
        }
    }

    @Override
    public Optional<Customer> find(String userName) {
        return getDatabase().find(userName);
    }

    @Override
    public Customer persist(Customer customer) {
        return getDatabase().persist(customer);
    }

    @Override
    public List<Customer> listAll() {
        return getDatabase().listAll();
    }

    @Override
    public Stream<Customer> listWhere(Predicate<Customer> predicate) {
        return getDatabase().listWhere(predicate);
    }

    @Override
    public CacheDBTable<String, Customer> getDatabase() {
        return database;
    }
}
