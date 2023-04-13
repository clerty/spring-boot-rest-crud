package ru.digitalhabits.homework3.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table(name = "department")
@SequenceGenerator(name = "default_gen", sequenceName = "department_pk_seq", allocationSize = 1)
@NamedQuery(name = "Department.findAll", query = "select d from Department d")
public class Department extends BaseEntity<Integer> {

    private String name;

    private boolean closed;

    //@JacksonBackReference
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private List<Person> persons;
}