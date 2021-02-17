package com.bootcamp.springchallenge.common.article.query;

import com.bootcamp.springchallenge.service.impl.article.query.OrderType;
import com.bootcamp.springchallenge.service.impl.article.query.ArticleQuery;

public class ArticleQueryForTest extends ArticleQuery {

    public void clear() {
        filters.clear();
    }

    public boolean isOrdered() {
        return orderType != OrderType.NONE;
    }

    public ArticleQueryForTest withoutOrder() {
        orderType = OrderType.NONE;
        return this;
    }
}
