package ru.digitalhabits.homework3.dao;

import org.springframework.stereotype.Component;
import ru.digitalhabits.homework3.domain.Department;

import javax.annotation.PostConstruct;

@Component
public class DepartmentDao
        extends BaseDao<Department, Integer> {

    @PostConstruct
    private void init() {
        this.setEntityClass(Department.class);
    }
}
