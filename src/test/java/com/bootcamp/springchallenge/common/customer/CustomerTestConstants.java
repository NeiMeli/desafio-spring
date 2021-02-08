package com.bootcamp.springchallenge.common.customer;

import com.bootcamp.springchallenge.controller.customer.dto.CustomerRequestDTO;
import com.bootcamp.springchallenge.entity.customer.Customer;

public class CustomerTestConstants {
    public static final Customer CUSTOMER_1 = new Customer("user1@gmail.com");
    public static final Customer CUSTOMER_2 = new Customer("user2@gmail.com");
    public static final CustomerRequestDTO CUSTOMER_REQUEST_WITH_PROVINCE = new CustomerRequestDTO().setUserName("user4@gmail.com").setProvince("salta");
    public static final CustomerRequestDTO CUSTOMER_REQUEST_WITHOUT_PROVINCE = new CustomerRequestDTO().setUserName("user5@gmail.com");
}
