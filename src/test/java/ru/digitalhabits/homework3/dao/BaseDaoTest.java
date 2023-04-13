package ru.digitalhabits.homework3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.digitalhabits.homework3.TestUtils;
import ru.digitalhabits.homework3.domain.BaseEntity;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BaseDaoTest<T extends BaseEntity<ID>, ID extends Serializable> {

    private BaseDao<T, ID> dao;
    private Class<T> entityClass;
    private Class<ID> idClass;

    @Autowired
    protected TestEntityManager entityManager;

    protected String notFoundMessage;
    protected Map<String, File> exampleFiles;

    protected void setup(BaseDao<T, ID> dao, Class<T> entityClass, Class<ID> idClass) {
        this.dao = dao;
        this.entityClass = entityClass;
        this.idClass = idClass;
        this.notFoundMessage = String.format(BaseDao.NOT_FOUND_MESSAGE_TEMPLATE, entityClass.getSimpleName());
        this.exampleFiles = TestUtils.retrieveExampleFiles(entityClass.getSimpleName().toLowerCase(Locale.ROOT), "model");
    }

    void findById_existing_ok(String entityFileName) throws IOException {
        T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
        ID id = this.entityManager.persistAndGetId(entity, idClass);

        assertEquals(entity, this.dao.findById(id));
    }

    void findById_nonExisting_exception(String entityFileName) throws IOException {
        T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
        ID id = this.entityManager.persistAndGetId(entity, idClass);
        this.entityManager.remove(entity);

        assertThrows(EntityNotFoundException.class, () -> this.dao.findById(id));
    }

    void findAll(String... entityFileNames) throws IOException {
        List<T> entityList = new ArrayList<>();
        for (String entityFileName: entityFileNames) {
            T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
            this.entityManager.persist(entity);
            entityList.add(entity);
        }

        assertArrayEquals(entityList.toArray(), this.dao.findAll().toArray());
    }

    void create(String entityFileName) throws IOException {
        T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
        ID id = this.dao.create(entity);

        assertEquals(entity, this.entityManager.find(entityClass, id));
    }

    void update(String entityFileName, String updatedEntityFileName) throws IOException {
        T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
        T updatedEntity = TestUtils.mapper.readValue(this.exampleFiles.get(updatedEntityFileName), entityClass);
        ID id = this.entityManager.persistAndGetId(entity, idClass);
        updatedEntity.setId(id);
        this.dao.update(updatedEntity);

        assertEquals(updatedEntity, this.entityManager.find(entityClass, id));
    }

    void delete_existing_ok(String entityFileName) throws IOException {
        T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
        ID id = this.entityManager.persistAndGetId(entity, idClass);
        this.dao.delete(id);

        assertNull(this.entityManager.find(entityClass, id));
    }

    void delete_nonExisting_ok(String entityFileName) throws IOException {
        T entity = TestUtils.mapper.readValue(this.exampleFiles.get(entityFileName), entityClass);
        ID id = this.entityManager.persistAndGetId(entity, idClass);
        this.entityManager.remove(entity);

        assertDoesNotThrow(() -> this.dao.delete(id));
    }
}
