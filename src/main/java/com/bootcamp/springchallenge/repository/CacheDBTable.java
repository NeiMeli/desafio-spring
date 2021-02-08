package com.bootcamp.springchallenge.repository;

import com.bootcamp.springchallenge.entity.Persistable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class CacheDBTable <K, V extends Persistable<K>> {
    private final Map<K, V> table;

    public CacheDBTable() {
        table = new HashMap<>();
    }

    public Optional<V> find(@NotNull final K key) {
        return Optional.ofNullable(table.get(key));
    }

    public V persist(@NotNull final V value) {
        if (value.isNew()) {
            value.setId(generateNextId());
        }
        table.put(value.getPrimaryKey(), value);
        return value;
    }

    @NotNull protected abstract K generateNextId();

    public List<V> listAll() {
        return new ArrayList<>(table.values());
    }

    public Stream<V> listWhere(Predicate<V> p) {
        return table.values().stream().filter(p);
    }

    public Optional<V> findFirst(Predicate<V> predicate) {
        return listWhere(predicate).findFirst();
    }

    protected int size() {
        return table.size();
    }
}
