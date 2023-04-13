package ru.digitalhabits.homework3.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.digitalhabits.homework3.TestUtils;
import ru.digitalhabits.homework3.domain.Department;
import ru.digitalhabits.homework3.domain.Person;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(PersonDao.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonDaoTest extends BaseDaoTest<Person, Integer> {

    @Autowired
    private PersonDao personDao;

    @BeforeAll
    void setup() {
        super.setup(this.personDao, Person.class, Integer.class);
    }

    @Test
    void findById_existing_ok() throws IOException {
        super.findById_existing_ok("personEntity1.json");
    }

    @Test
    void findById_nonExisting_exception() throws Exception {
        super.findById_nonExisting_exception("personEntity1.json");
    }

    @Test
    void findAll() throws IOException {
        super.findAll("personEntity1.json", "personEntity2.json");
    }

    @Test
    void create() throws IOException {
        super.create("personEntity1.json");
    }

    @Test
    void update() throws IOException {
        super.update("personEntity1.json", "personEntity2.json");
    }

    @Test
    void delete_existing_ok() throws IOException {
        super.delete_existing_ok("personEntity1.json");
    }

    @Test
    void delete_nonExisting_ok() throws IOException {
        super.delete_nonExisting_ok("personEntity1.json");
    }

    @Test
    void findByIdAndDepartment_existing_ok() throws IOException {
        Person person = TestUtils.mapper.readValue(this.exampleFiles.get("personEntityWithDepartment.json"), Person.class);
        this.entityManager.persist(person.getDepartment().setId(null));
        Integer personId = this.entityManager.persistAndGetId(person, Integer.class);

        assertEquals(person, this.personDao.findByIdAndDepartment(personId, person.getDepartment()));
    }

    @Test
    void findByIdAndDepartment_nonExistingPerson_exception() {
        assertThrows(EntityNotFoundException.class, () -> this.personDao.findByIdAndDepartment(1, (Department) new Department().setId(1)));
    }

    @Test
    void findByIdAndDepartment_nonExistingPersonWithPresentedDepartment_exception() throws IOException {
        Person person = TestUtils.mapper.readValue(this.exampleFiles.get("personEntityWithDepartment.json"), Person.class);
        this.entityManager.persist(person.getDepartment().setId(null));
        Integer personId = this.entityManager.persistAndGetId(person, Integer.class);

        assertThrows(EntityNotFoundException.class, () -> this.personDao.findByIdAndDepartment(personId, (Department) new Department().setId(2)));
    }
}