package ru.digitalhabits.homework3.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@MappedSuperclass
@Accessors(chain = true)
public class BaseEntity<ID extends Serializable> { //override eq hash

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_gen")
    private ID id;
}
