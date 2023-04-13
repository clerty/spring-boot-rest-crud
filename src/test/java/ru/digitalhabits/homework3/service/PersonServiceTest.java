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
import ru.digitalhabits.homework3.model.PersonFullResponse;
import ru.digitalhabits.homework3.model.PersonRequest;
import ru.digitalhabits.homework3.model.PersonShortResponse;

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
class PersonServiceTest {

    @MockBean
    PersonDao personDao;

    @MockBean
    DepartmentDao departmentDao;

    @Autowired
    PersonService personService;

    static Map<String, File> exampleFiles;
    static final int personId = 1;
    static final int departmentId = 1;

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
        List<String> personFileNames = List.of("personEntity1.json", "personEntity2.json");
        List<Person> personList = new ArrayList<>();
        for (String personFileName: personFileNames) {
            Person person = TestUtils.mapper.readValue(exampleFiles.get(personFileName), Person.class);
            personList.add(person);
        }
        List<PersonShortResponse> personShortResponseList = List.of(
                TestUtils.mapper.readValue(exampleFiles.get("personShortResponse1.json"), PersonShortResponse.class),
                TestUtils.mapper.readValue(exampleFiles.get("personShortResponse2.json"), PersonShortResponse.class)
        );

        when(this.personDao.findAll())
                .thenReturn(personList);
        assertArrayEquals(personShortResponseList.toArray(), this.personService.findAll().toArray());
    }

    @Test
    void findById_existing_ok() throws IOException {
        int age = 20;
        Person person = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class);
        person.setAge(age).setId(personId);
        PersonFullResponse personFullResponse = TestUtils.mapper.readValue(exampleFiles.get("personFullResponse.json"), PersonFullResponse.class);

        when(this.personDao.findById(personId))
                .thenReturn(person);
        assertEquals(personFullResponse, this.personService.getById(personId));
    }

    @Test
    void findById_nonExisting_exception() {
        when(this.personDao.findById(personId))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> this.personService.getById(personId));
    }

    @Test
    void create() throws IOException {
        PersonRequest personRequest = TestUtils.mapper.readValue(exampleFiles.get("personRequest.json"), PersonRequest.class);
        Person toCreatePerson = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class);

        when(this.personDao.create(toCreatePerson))
                .then(invocation -> {
                    Person person = invocation.getArgument(0);
                    person.setId(personId);
                    return personId;
                });
        assertEquals(personId, this.personService.create(personRequest));
    }

    @Test
    void addPersonToDepartment_existing_ok() throws IOException {
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);
        Person person = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class).setDepartment(department);
        Person personToAdd = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class);

        when(this.departmentDao.findById(departmentId))
                .thenReturn(department);
        when(this.personDao.findById(personId))
                .thenReturn(personToAdd);
        when(this.personDao.update(personToAdd))
                .thenReturn(person);

        assertDoesNotThrow(() -> this.personService.addPersonToDepartment(departmentId, personId));
    }

    @Test
    void addPersonToDepartment_nonExistingPerson_exception() throws IOException {
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);

        when(this.departmentDao.findById(departmentId))
                .thenReturn(department);
        when(this.personDao.findById(personId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> this.personService.addPersonToDepartment(departmentId, personId));
    }

    @Test
    void addPersonToDepartment_nonExistingDepartment_exception() {
        when(this.departmentDao.findById(departmentId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> this.personService.addPersonToDepartment(departmentId, personId));
    }

    @Test
    void addPersonToDepartment_closedDepartment_exception() throws IOException {
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class).setClosed(true);

        when(this.departmentDao.findById(departmentId))
                .thenReturn(department);
        assertThrows(IllegalStateException.class, () -> this.personService.addPersonToDepartment(departmentId, personId));
    }

    @Test
    void update() throws IOException {
        int age = 20;
        PersonRequest personRequest = new PersonRequest().setAge(age);
        Person person = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class);
        Person toUpdatePerson = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class).setAge(age);
        Person updatedPerson = (Person) TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class).setAge(age).setId(personId);
        PersonFullResponse personFullResponse = TestUtils.mapper.readValue(exampleFiles.get("personFullResponse.json"), PersonFullResponse.class);

        when(this.personDao.findById(personId))
                .thenReturn(person);
        when(this.personDao.update(toUpdatePerson))
                .thenReturn(updatedPerson);
        assertEquals(personFullResponse, this.personService.update(personId, personRequest));
    }

    @Test
    void delete() {
        assertDoesNotThrow(() -> this.personService.delete(personId));
    }

    @Test
    void removePersonFromDepartment_existing_ok() throws IOException {
        Person person = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class);
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);
        Person personToRemove = TestUtils.mapper.readValue(exampleFiles.get("personEntity1.json"), Person.class).setDepartment(department);

        when(this.departmentDao.findById(departmentId))
                .thenReturn(department);
        when(this.personDao.findByIdAndDepartment(personId, department))
                .thenReturn(personToRemove);
        when(this.personDao.update(personToRemove))
                .thenReturn(person);

        assertDoesNotThrow(() -> this.personService.removePersonFromDepartment(departmentId, personId));
    }

    @Test
    void removePersonFromDepartment_nonExistingPerson_ok() throws IOException {
        Department department = TestUtils.mapper.readValue(exampleFiles.get("departmentEntity1.json"), Department.class);

        when(this.departmentDao.findById(departmentId))
                .thenReturn(department);
        when(this.personDao.findByIdAndDepartment(personId, department))
                .thenThrow(EntityNotFoundException.class);

        assertDoesNotThrow(() -> this.personService.removePersonFromDepartment(departmentId, personId));
    }

    @Test
    void removePersonFromDepartment_nonExistingDepartment_exception() {
        when(this.departmentDao.findById(departmentId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> this.personService.removePersonFromDepartment(departmentId, personId));
    }
}