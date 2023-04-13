package ru.digitalhabits.homework3.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.digitalhabits.homework3.TestUtils;
import ru.digitalhabits.homework3.dao.DepartmentDao;
import ru.digitalhabits.homework3.dao.PersonDao;
import ru.digitalhabits.homework3.domain.Department;
import ru.digitalhabits.homework3.domain.Person;
import ru.digitalhabits.homework3.model.DepartmentFullResponse;
import ru.digitalhabits.homework3.model.DepartmentRequest;
import ru.digitalhabits.homework3.model.DepartmentShortResponse;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DepartmentServiceTest {

    @MockBean
    PersonDao personDao;

    @MockBean
    DepartmentDao departmentDao;

    @Autowired
    DepartmentService departmentService;

    static Map<String, File> exampleFiles;

    @BeforeAll
    static void setup() {
        exampleFiles = Stream.of(
                        TestUtils.retrieveExampleFiles("person", "model"),
                        TestUtils.retrieveExampleFiles("person", "domain"),
                        TestUtils.retrieveExampleFiles("department", "model"),
                        TestUtils.retrieveExampleFiles("department", "domain")
                )
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Test
    void findAll() throws IOException {
        List<String> departmentFileNames = List.of("departmentEntity1.json", "departmentEntity2.json");
        List<Department> departmentList = new ArrayList<>();
        for (String departmentFileName: departmentFileNames) {
            Department department = TestUtils.mapper.readValue(exampleFiles.get(departmentFileName), Department.class);
            departmentList.add(department);
        }
        List<DepartmentShortResponse> departmentShortResponseList = List.of(
                TestUtils.mapper.readValue(exampleFiles.get("departmentShortResponse1.json"), DepartmentShortResponse.class),
                TestUtils.mapper.readValue(exampleFiles.get("departmentShortResponse2.json"), DepartmentShortResponse.class)
        );

        when(this.departmentDao.findAll())
                .thenReturn(departmentList);
        assertArrayEquals(departmentShortResponseList.toArray(), this.departmentService.findAll().toArray());
    }

    @Test
    void findById_existing_ok() throws IOException {
        int id = 1;
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);
        department.setId(id);
        DepartmentFullResponse departmentFullResponse = TestUtils.mapper.readValue(exampleFiles.get("departmentFullResponse.json"), DepartmentFullResponse.class);

        when(this.departmentDao.findById(id))
                .thenReturn(department);
        assertEquals(departmentFullResponse, this.departmentService.getById(id));
    }

    @Test
    void findById_nonExisting_exception() {
        int id = 1;

        when(this.departmentDao.findById(id))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> this.departmentService.getById(id));
    }

    @Test
    void create() throws IOException {
        int id = 1;
        DepartmentRequest departmentRequest = TestUtils.mapper.readValue(exampleFiles.get("departmentRequest.json"), DepartmentRequest.class);
        Department toCreateDepartment = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);

        when(this.departmentDao.create(toCreateDepartment))
                .then(invocation -> {
                    Department department = invocation.getArgument(0);
                    department.setId(id);
                    return id;
                });

        assertEquals(id, this.departmentService.create(departmentRequest));
    }

    @Test
    void update() throws IOException {
        int id = 1;
        String name = "bar";
        DepartmentRequest departmentRequest = new DepartmentRequest().setName(name);
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);
        Department toUpdateDepartment = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class).setName(name);
        Department updatedDepartment = (Department) TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class).setName(name).setId(id);
        DepartmentFullResponse departmentFullResponse = TestUtils.mapper.readValue(exampleFiles.get("departmentFullResponse.json"), DepartmentFullResponse.class).setName(name);

        when(this.departmentDao.findById(id))
                .thenReturn(department);
        when(this.departmentDao.update(toUpdateDepartment))
                .thenReturn(updatedDepartment);
        assertEquals(departmentFullResponse, this.departmentService.update(id, departmentRequest));
    }

    @Test
    void delete() {
        int id = 1;

        assertDoesNotThrow(() -> this.departmentService.delete(id));
    }

    @Test
    void close_existing_ok() throws IOException {
        int id = 1; //field
        Person person = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class);
        Department department = (Department) TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class).setPersons(List.of(person)).setId(id);
        Department departmentClosed = (Department) TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class).setPersons(List.of()).setClosed(true).setId(id);

        when(this.departmentDao.findById(id))
                .thenReturn(department);
        when(this.personDao.update(person))
                .thenReturn(person);
        when(this.departmentDao.update(departmentClosed))
                .thenReturn(departmentClosed);
        //verify
        assertDoesNotThrow(() -> this.departmentService.close(id));
    }

    @Test
    void close_nonExisting_exception(){
        int id = 1;

        when(this.departmentDao.findById(id))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> this.departmentService.close(id));
    }
}