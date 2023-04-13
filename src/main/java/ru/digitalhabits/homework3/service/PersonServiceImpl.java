package ru.digitalhabits.homework3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabits.homework3.dao.DepartmentDao;
import ru.digitalhabits.homework3.dao.PersonDao;
import ru.digitalhabits.homework3.domain.Department;
import ru.digitalhabits.homework3.domain.Person;
import ru.digitalhabits.homework3.model.PersonFullResponse;
import ru.digitalhabits.homework3.model.PersonRequest;
import ru.digitalhabits.homework3.model.PersonShortResponse;
import ru.digitalhabits.homework3.service.mapper.PersonMapper;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl
        implements PersonService {

    private static final String DEPARTMENT_CLOSED_MESSAGE = "Department with presented id is closed";

    private final PersonDao personDao;
    private final DepartmentDao departmentDao;
    private final PersonMapper personMapper;

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public List<PersonShortResponse> findAll() {
        return personMapper.personsListToPersonShortResponsesList(personDao.findAll());
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public PersonFullResponse getById(int id) {
        return personMapper.personToPersonFullResponse(personDao.findById(id));
    }

    @Override
    @Transactional
    public int create(@Nonnull PersonRequest request) {
        Person person = new Person();
        personMapper.personRequestToPerson(request, person);
        return personDao.create(person);
    }

    @Nonnull
    @Override
    @Transactional
    public PersonFullResponse update(int id, @Nonnull PersonRequest request) {
        Person personToUpdate = personDao.findById(id);
        personMapper.personRequestToPerson(request, personToUpdate);
        Person personUpdated = personDao.update(personToUpdate);
        return personMapper.personToPersonFullResponse(personUpdated);
    }

    @Override
    @Transactional
    public void delete(int id) {
        personDao.delete(id);
    }


    @Override
    @Transactional
    public void addPersonToDepartment(int departmentId, int personId) {
        Department department = departmentDao.findById(departmentId);
        if (department.isClosed())
            throw new IllegalStateException(DEPARTMENT_CLOSED_MESSAGE);

        Person person = personDao.findById(personId);
        person.setDepartment(department);
        personDao.update(person);
    }

    @Override
    @Transactional
    public void removePersonFromDepartment(int departmentId, int personId) {
        Department department = departmentDao.findById(departmentId);
        try {
            Person person = personDao.findByIdAndDepartment(personId, department);
            person.setDepartment(null);
            personDao.update(person);
        } catch (EntityNotFoundException exception) {
            log.error("", exception);
        }
    }
}
