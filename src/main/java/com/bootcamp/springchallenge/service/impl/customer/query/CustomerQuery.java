package com.bootcamp.springchallenge.service.impl.customer.query;

import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.entity.customer.Province;
import com.bootcamp.springchallenge.service.impl.Query;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bootcamp.springchallenge.service.impl.customer.query.CustomerQueryParam.PROVINCE;

public class CustomerQuery extends Query<CustomerQueryParam, Customer> {

    public CustomerQuery withProvinces(@Nullable String ... provinces) {
        if (provinces != null && provinces.length > 0) {
            List<Province> provinceList = Arrays.stream(provinces).filter(Objects::nonNull).map(Province::fromLabel).collect(Collectors.toList());
            filters.put(PROVINCE, c -> provinceList.contains(c.getProvince()));
        }
        return this;
    }
}
