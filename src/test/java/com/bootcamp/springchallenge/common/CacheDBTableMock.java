package com.bootcamp.springchallenge.common;

import com.bootcamp.springchallenge.entity.Persistable;
import com.bootcamp.springchallenge.repository.CacheDBTable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CacheDBTableMock<K, V extends Persistable<K>> extends CacheDBTable<K, V> {
    public CacheDBTableMock(List<V> elements) {
        super();
        elements.forEach(e -> table.put(e.getPrimaryKey(), e));
    }

    @Override
    protected @NotNull K generateNextId() {
        throw new RuntimeException("Not implemented");
    }
}
