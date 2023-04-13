package ru.digitalhabits.homework3.service;

import javax.annotation.Nonnull;
import java.util.List;

public interface CrudService<T, S, R> {
    @Nonnull
    List<S> findAll();

    @Nonnull
    R getById(int id);

    int create(@Nonnull T request);

    @Nonnull
    R update(int id, @Nonnull T request);

    void delete(int id);
}
