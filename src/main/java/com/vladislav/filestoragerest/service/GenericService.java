package com.vladislav.filestoragerest.service;

import java.util.List;

public interface GenericService<T, ID> {
    List<T> getAll();
    T getById(ID id);
}
