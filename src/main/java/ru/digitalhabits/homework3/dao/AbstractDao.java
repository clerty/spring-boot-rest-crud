package ru.digitalhabits.homework3.dao;

import ru.digitalhabits.homework3.domain.BaseEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractDao<T extends BaseEntity<ID>, ID extends Serializable> implements CrudOperations<T, ID> { //-abstract +prot constr

    public static final String NOT_FOUND_MESSAGE_TEMPLATE = "%s with presented id is not found";

    private Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager; //final

    protected RuntimeException makeNotFoundException() {
        return new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE_TEMPLATE, entityClass.getSimpleName()));
    }

    protected final void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Nullable
    @Override
    public T findById(@Nonnull ID id) {
        Class<T> entity2 = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]; //TODO
        T entity = entityManager.find(entityClass, id);
        if (entity == null)
            throw this.makeNotFoundException();
        return entity;
    }

    @Nonnull
    @Override
    public List<T> findAll() {
        return entityManager.createNamedQuery(entityClass.getSimpleName() + ".findAll", entityClass).getResultList();
    }

    @Override
    public ID create(@Nonnull T entity) {
        entityManager.persist(entity);
        return entity.getId();
    }

    @Nonnull
    @Override
    public T update(@Nonnull T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void delete(@Nonnull ID id) {
        try {
            entityManager.remove(this.findById(id));
        } catch (EntityNotFoundException ignored) {} //logger
    }
}
