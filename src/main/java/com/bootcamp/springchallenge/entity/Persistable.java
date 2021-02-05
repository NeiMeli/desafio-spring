package com.bootcamp.springchallenge.entity;

import org.jetbrains.annotations.NotNull;

public interface Persistable <K> {
    K getPrimaryKey();
    boolean isNew();
    void setId(@NotNull K id);
}
