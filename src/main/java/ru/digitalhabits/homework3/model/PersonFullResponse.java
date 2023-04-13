package ru.digitalhabits.homework3.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PersonFullResponse {
    private Integer id;
    private String fullName;
    private Integer age;
    private DepartmentShortResponse department;
}
