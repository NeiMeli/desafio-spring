package com.bootcamp.springchallenge.repository;

import com.bootcamp.springchallenge.entity.Persistable;

public interface CacheRepository<K, V extends Persistable<K>> {
    CacheDBTable<K, V> getDatabase();
}
