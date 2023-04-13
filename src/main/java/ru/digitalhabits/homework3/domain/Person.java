package ru.digitalhabits.homework3.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Entity
@Table(name = "person")
@SequenceGenerator(name = "default_gen", sequenceName = "person_pk_seq", allocationSize = 1)
@NamedQuery(name = "Person.findAll", query = "select p from Person p")
@NamedQuery(name = "Person.findByIdAndDepartment", query = "select p from Person p where p.id = :personId and p.department = :department")
public class Person extends BaseEntity<Integer> {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    private Integer age;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
