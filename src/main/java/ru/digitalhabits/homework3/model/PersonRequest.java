package ru.digitalhabits.homework3.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.digitalhabits.homework3.model.validation.group.Create;

import javax.validation.constraints.*;
import javax.validation.groups.Default;

@Data
@Accessors(chain = true)
public class PersonRequest {

    @NotEmpty(groups = Create.class, message = "{field.is.empty}")
    private String firstName;

    private String middleName;

    @NotEmpty(groups = Create.class, message = "{field.is.empty}")
    private String lastName;

    @Min(value = 18, groups = {Create.class, Default.class}, message = "{field.min.value}")
    @Max(value = 65, groups = {Create.class, Default.class}, message = "{field.max.value}")
    private Integer age;
}
