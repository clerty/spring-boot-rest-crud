package ru.digitalhabits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabits.homework3.dao.DepartmentDao;
import ru.digitalhabits.homework3.dao.PersonDao;
import ru.digitalhabits.homework3.domain.Department;
import ru.digitalhabits.homework3.domain.Person;
import ru.digitalhabits.homework3.model.DepartmentFullResponse;
import ru.digitalhabits.homework3.model.DepartmentRequest;
import ru.digitalhabits.homework3.model.DepartmentShortResponse;
import ru.digitalhabits.homework3.service.mapper.DepartmentMapper;

import javax.annotation.Nonnull;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl
        implements DepartmentService {

    private final PersonDao personDao;
    private final DepartmentDao departmentDao;
    private final DepartmentMapper departmentMapper;

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentShortResponse> findAll() {
        return departmentMapper.departmentsListToDepartmentShortResponsesList(departmentDao.findAll());
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public DepartmentFullResponse getById(int id) {
        return departmentMapper.departmentToDepartmentFullResponse(departmentDao.findById(id));
    }

    @Override
    @Transactional
    public int create(@Nonnull DepartmentRequest request) {
        Department department = new Department();
        departmentMapper.departmentRequestToDepartment(request, department);
        return departmentDao.create(department);
    }

    @Nonnull
    @Override
    @Transactional
    public DepartmentFullResponse update(int id, @Nonnull DepartmentRequest request) {
        Department departmentToUpdate = departmentDao.findById(id);
        departmentMapper.departmentRequestToDepartment(request, departmentToUpdate);
        Department departmentUpdated = departmentDao.update(departmentToUpdate);
        return departmentMapper.departmentToDepartmentFullResponse(departmentUpdated);
    }

    @Override
    @Transactional
    public void delete(int id) {
        departmentDao.delete(id);
    }

    @Override
    @Transactional
    public void close(int id) {
        Department department = departmentDao.findById(id);
        for (Person person : department.getPersons()) {
            person.setDepartment(null);
            personDao.update(person);
        }
        department.setClosed(true);
        departmentDao.update(department);
    }
}
