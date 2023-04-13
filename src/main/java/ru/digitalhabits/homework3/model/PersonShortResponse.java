package ru.digitalhabits.homework3.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PersonShortResponse {
    private Integer id;
    private String fullName;
}
