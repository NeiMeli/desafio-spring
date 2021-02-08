package com.bootcamp.springchallenge.service.impl.customer;

import com.bootcamp.springchallenge.common.customer.CustomerTestConstants;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.controller.customer.dto.CustomerResponseDTO;
import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.entity.customer.Province;
import com.bootcamp.springchallenge.repository.CustomerRepository;
import com.bootcamp.springchallenge.service.impl.customer.exception.CustomerServiceErrorImpl;
import com.bootcamp.springchallenge.service.impl.customer.exception.CustomerServiceException;
import com.bootcamp.springchallenge.service.impl.customer.query.CustomerQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerServiceImplTest {
    @Autowired
    CustomerServiceImpl service;

    @Autowired
    CustomerRepository repository;

    @Test
    void testBadRequests() {
        // create
        CustomerRequestDTO invalidRequest = new CustomerRequestDTO(); // sin nombre
        assertThatExceptionOfType(CustomerServiceException.class)
                .isThrownBy(() -> service.create(invalidRequest))
                .withMessageContaining(CustomerServiceErrorImpl.EMPTY_USERNAME.getMessage());
        invalidRequest.setUserName(CustomerTestConstants.CUSTOMER_1.getUserName()); // usuario ya existe
        assertThatExceptionOfType(CustomerServiceException.class)
                .isThrownBy(() -> service.create(invalidRequest))
                .withMessageContaining(CustomerServiceErrorImpl.CUSTOMER_ALREADY_EXISTS.getMessage());
        invalidRequest.setProvince("invalid-province");
        assertThatExceptionOfType(Province.ProvinceNotFoundException.class)
                .isThrownBy(() -> service.create(invalidRequest))
                .withMessageContaining("invalid-province");

        // update
        CustomerRequestDTO invalidRequest2 = new CustomerRequestDTO(); // sin nombre
        assertThatExceptionOfType(CustomerServiceException.class)
                .isThrownBy(() -> service.update(invalidRequest2))
                .withMessageContaining(CustomerServiceErrorImpl.EMPTY_USERNAME.getMessage());
        invalidRequest.setUserName("non-existant-user"); // usuario no existe
        assertThatExceptionOfType(CustomerServiceException.class)
                .isThrownBy(() -> service.update(invalidRequest))
                .withMessageContaining(CustomerServiceErrorImpl.CUSTOMER_NOT_FOUND.getMessage());
        invalidRequest2.setUserName(CustomerTestConstants.CUSTOMER_1.getUserName()).setProvince("invalid-province");
        assertThatExceptionOfType(Province.ProvinceNotFoundException.class)
                .isThrownBy(() -> service.update(invalidRequest2))
                .withMessageContaining("invalid-province");
    }

    @Test
    void testCreateHappy() {
        // con provincia
        CustomerRequestDTO request = CustomerTestConstants.CUSTOMER_REQUEST_WITH_PROVINCE;
        CustomerResponseDTO response = service.create(request);
        assertThat(response.getUserName()).isEqualToIgnoringCase(request.getUserName());
        assertThat(response.getProvince()).isEqualToIgnoringCase(request.getProvince());
        // veamos la base de datos
        Optional<Customer> customerOpt = repository.find(request.getUserName());
        assertThat(customerOpt).isPresent();
        Customer customer = customerOpt.get();
        assertThat(customer.getUserName()).isEqualToIgnoringCase(request.getUserName());
        assertThat(customer.getProvince().getLabel()).isEqualToIgnoringCase(request.getProvince());

        // sin provincia
        CustomerRequestDTO request2 = CustomerTestConstants.CUSTOMER_REQUEST_WITHOUT_PROVINCE;
        CustomerResponseDTO response2 = service.create(request2);
        assertThat(response2.getUserName()).isEqualToIgnoringCase(request2.getUserName());
        assertThat(response2.getProvince()).isEqualToIgnoringCase(Province.UNDEFINED.getLabel());
        // veamos la base de datos
        Optional<Customer> customerOpt2 = repository.find(request2.getUserName());
        assertThat(customerOpt2).isPresent();
        Customer customer2 = customerOpt2.get();
        assertThat(customer2.getUserName()).isEqualToIgnoringCase(request2.getUserName());
        assertThat(customer2.getProvince()).isEqualTo(Province.UNDEFINED);
    }

    @Test
    void testUpdateHappy() {
        CustomerRequestDTO request = CustomerTestConstants.CUSTOMER_REQUEST_WITHOUT_PROVINCE;
        service.create(request);
        request.setProvince(Province.MENDOZA.getLabel());
        CustomerResponseDTO updateResponse = service.update(request);
        assertThat(updateResponse.getUserName()).isEqualTo(request.getUserName());
        assertThat(updateResponse.getProvince()).isEqualTo(Province.MENDOZA.getLabel());

        // veamos la base de datos
        Optional<Customer> customerOpt = repository.find(request.getUserName());
        assertThat(customerOpt).isPresent();
        Customer customer = customerOpt.get();
        assertThat(customer.getUserName()).isEqualToIgnoringCase(request.getUserName());
        assertThat(customer.getProvince()).isEqualTo(Province.MENDOZA);

        request.setProvince(null);
        CustomerResponseDTO updateResponse2 = service.update(request);
        assertThat(updateResponse2.getUserName()).isEqualTo(request.getUserName());
        assertThat(updateResponse2.getProvince()).isEqualTo(Province.UNDEFINED.getLabel());

        // veamos la base de datos
        Optional<Customer> customerOpt2 = repository.find(request.getUserName());
        assertThat(customerOpt2).isPresent();
        Customer customer2 = customerOpt2.get();
        assertThat(customer2.getUserName()).isEqualToIgnoringCase(request.getUserName());
        assertThat(customer2.getProvince()).isEqualTo(Province.UNDEFINED);
    }

    @Test
    void testCustomerQuery() {
        // guardo un par de customers con distintas provincias
        List<CustomerRequestDTO> requestList = new ArrayList<>();
        requestList.add(new CustomerRequestDTO().setUserName("user7").setProvince(Province.MENDOZA.getLabel()));
        requestList.add(new CustomerRequestDTO().setUserName("user8").setProvince(Province.MENDOZA.getLabel()));
        requestList.add(new CustomerRequestDTO().setUserName("user9").setProvince(Province.MENDOZA.getLabel()));
        requestList.add(new CustomerRequestDTO().setUserName("user10").setProvince(Province.CORDOBA.getLabel()));
        requestList.add(new CustomerRequestDTO().setUserName("user11").setProvince(Province.CORDOBA.getLabel()));
        requestList.add(new CustomerRequestDTO().setUserName("user12").setProvince(Province.UNDEFINED.getLabel()));

        // creamos los customer
        requestList.forEach(r -> service.create(r));

        // tomo los nombres para las comparaciones
        List<String> allRequestNames = requestList.stream().map(r -> r.getUserName().toLowerCase()).collect(Collectors.toList());
        List<String> mendozaRequestNames = requestList.stream().filter(r -> r.getProvince().equals(Province.MENDOZA.getLabel())).map(r -> r.getUserName().toLowerCase()).collect(Collectors.toList());
        List<String> cordobaRequestNames = requestList.stream().filter(r -> r.getProvince().equals(Province.CORDOBA.getLabel())).map(r -> r.getUserName().toLowerCase()).collect(Collectors.toList());


        CustomerQuery query = new CustomerQuery();
        Function<List<CustomerResponseDTO>, List<String>> responseToNameList = list -> list.stream().map(r -> r.getUserName().toLowerCase()).collect(Collectors.toList());
        // si la uso vacia deberia traerme todos
        List<CustomerResponseDTO> response1 = service.query(query);
        assertThat(allRequestNames.stream()).allMatch(n -> responseToNameList.apply(response1).contains(n));
        // solo Mendoza
        query.withProvinces(Province.MENDOZA.getLabel());
        List<CustomerResponseDTO> response2 = service.query(query);
        assertThat(mendozaRequestNames.stream()).allMatch(n -> responseToNameList.apply(response2).contains(n));
        assertThat(cordobaRequestNames.stream()).allMatch(n -> !responseToNameList.apply(response2).contains(n));
        // solo Cordoba
        query.withProvinces(Province.CORDOBA.getLabel());
        List<CustomerResponseDTO> response3 = service.query(query);
        assertThat(cordobaRequestNames.stream()).allMatch(n -> responseToNameList.apply(response3).contains(n));
        assertThat(mendozaRequestNames.stream()).allMatch(n -> !responseToNameList.apply(response3).contains(n));
        // Mendoza y Cordoba
        query.withProvinces(Province.MENDOZA.getLabel(), Province.CORDOBA.getLabel());
        List<CustomerResponseDTO> response4 = service.query(query);
        assertThat(mendozaRequestNames.stream()).allMatch(n -> responseToNameList.apply(response4).contains(n));
        assertThat(cordobaRequestNames.stream()).allMatch(n -> responseToNameList.apply(response4).contains(n));
    }
}