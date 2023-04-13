package ru.digitalhabits.homework3.service;

import ru.digitalhabits.homework3.model.PersonFullResponse;
import ru.digitalhabits.homework3.model.PersonRequest;
import ru.digitalhabits.homework3.model.PersonShortResponse;

public interface PersonService extends CrudService<PersonRequest, PersonShortResponse, PersonFullResponse> {

    void addPersonToDepartment(int departmentId, int personId);

    void removePersonFromDepartment(int departmentId, int personId);
}
