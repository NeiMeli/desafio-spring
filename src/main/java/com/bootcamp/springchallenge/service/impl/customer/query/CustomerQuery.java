package com.bootcamp.springchallenge.service.impl.customer.query;

import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.entity.customer.Province;
import com.bootcamp.springchallenge.service.impl.Query;
import org.jetbrains.annotations.Nullable;

import static com.bootcamp.springchallenge.service.impl.customer.query.CustomerQueryParam.PROVINCE;

public class CustomerQuery extends Query<CustomerQueryParam, Customer> {
    public CustomerQuery withProvince(@Nullable String provinceLabel) {
        if (provinceLabel != null) {
            Province province = Province.fromLabel(provinceLabel);
            filters.put(PROVINCE, c -> c.getProvince() == province);
        }
        return this;
    }
}
