package com.author.crm.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    T create(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();
}
