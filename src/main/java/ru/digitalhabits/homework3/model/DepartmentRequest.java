package ru.digitalhabits.homework3.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class DepartmentRequest {
    @NotEmpty(message = "{field.is.empty}")
    private String name;
}
