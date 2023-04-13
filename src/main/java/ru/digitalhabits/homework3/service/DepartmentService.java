package ru.digitalhabits.homework3.service;

import ru.digitalhabits.homework3.model.DepartmentFullResponse;
import ru.digitalhabits.homework3.model.DepartmentRequest;
import ru.digitalhabits.homework3.model.DepartmentShortResponse;

public interface DepartmentService extends CrudService<DepartmentRequest, DepartmentShortResponse, DepartmentFullResponse> {

    void close(int id);
}
