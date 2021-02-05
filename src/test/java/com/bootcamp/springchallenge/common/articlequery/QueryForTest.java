package com.bootcamp.springchallenge.common.articlequery;

import com.bootcamp.springchallenge.service.impl.query.OrderType;
import com.bootcamp.springchallenge.service.impl.query.Query;
import com.bootcamp.springchallenge.service.impl.query.QueryParam;

public class QueryForTest extends Query {

    public void clear() {
        filters.clear();
    }

    public QueryForTest without(QueryParam param) {
        filters.remove(param);
        return this;
    }

    public boolean isOrdered() {
        return orderType != OrderType.NONE;
    }

    public QueryForTest withoutOrder() {
        orderType = OrderType.NONE;
        return this;
    }
}
