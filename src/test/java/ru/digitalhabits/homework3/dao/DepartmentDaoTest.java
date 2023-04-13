package ru.digitalhabits.homework3.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.digitalhabits.homework3.domain.Department;

import java.io.IOException;

@DataJpaTest
@Import(DepartmentDao.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepartmentDaoTest extends BaseDaoTest<Department, Integer> {

    @Autowired
    private DepartmentDao departmentDao;

    @BeforeAll
    void setup() {
        super.setup(departmentDao, Department.class, Integer.class);
    }

    @Test
    void findById_existing_ok() throws IOException {
        super.findById_existing_ok("departmentEntity1.json");
    }

    @Test
    void findById_nonExisting_exception() throws Exception {
        super.findById_nonExisting_exception("departmentEntity1.json");
    }

    @Test
    void findAll() throws IOException {
        super.findAll("departmentEntity1.json", "departmentEntity2.json");
    }

    @Test
    void create() throws IOException {
        super.create("departmentEntity1.json");
    }

    @Test
    void update() throws IOException {
        super.update("departmentEntity1.json", "departmentEntity2.json");
    }

    @Test
    void delete_existing_ok() throws IOException {
        super.delete_existing_ok("departmentEntity1.json");
    }

    @Test
    void delete_nonExisting_ok() throws IOException {
        super.delete_nonExisting_ok("departmentEntity1.json");
    }
}