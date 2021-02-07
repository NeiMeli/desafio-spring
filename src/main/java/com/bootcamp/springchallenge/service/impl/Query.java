package com.bootcamp.springchallenge.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class Query <P, E> {
    protected final Map<P, Predicate<E>> filters;

    public Query() {
        this.filters = new HashMap<>();
    }

    public Predicate<E> buildPredicate() {
        return e -> filters.values().stream().allMatch(p -> p.test(e));
    }

    public Comparator<E> getComparator() {
        return (o1, o2) -> 0;
    }
}
