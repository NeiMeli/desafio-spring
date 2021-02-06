package com.bootcamp.springchallenge.common.articlequery;

import com.bootcamp.springchallenge.service.impl.query.OrderType;
import com.bootcamp.springchallenge.service.impl.query.Query;

public class QueryForTest extends Query {

    public void clear() {
        filters.clear();
    }

    public boolean isOrdered() {
        return orderType != OrderType.NONE;
    }

    public QueryForTest withoutOrder() {
        orderType = OrderType.NONE;
        return this;
    }
}
