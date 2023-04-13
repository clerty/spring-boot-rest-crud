package ru.digitalhabits.homework3.dao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface CrudOperations<T, ID> {

    @Nullable
    T findById(@Nonnull ID id);

    @Nonnull
    List<T> findAll();

    ID create(@Nonnull T entity);

    @Nonnull
    T update(@Nonnull T entity);

    void delete(@Nonnull ID id);
}
