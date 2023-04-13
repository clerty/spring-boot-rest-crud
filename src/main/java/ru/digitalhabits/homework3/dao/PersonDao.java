package ru.digitalhabits.homework3.dao;

import org.springframework.stereotype.Component;
import ru.digitalhabits.homework3.domain.Department;
import ru.digitalhabits.homework3.domain.Person;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

@Component
public class PersonDao
        extends AbstractDao<Person, Integer> {

    @PostConstruct
    private void init() {
        this.setEntityClass(Person.class);
    }

    public Person findByIdAndDepartment(@Nonnull Integer personId, @Nonnull Department department) {
        return entityManager.createNamedQuery("Person.findByIdAndDepartment", Person.class)
                .setParameter("personId", personId)
                .setParameter("department", department)
                .getResultList()
                .stream()
                .findFirst()
                .orElseThrow(this::makeNotFoundException);
    }
}