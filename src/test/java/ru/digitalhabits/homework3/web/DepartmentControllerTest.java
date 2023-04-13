package ru.digitalhabits.homework3.web;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ru.digitalhabits.homework3.dao.BaseDao;
import ru.digitalhabits.homework3.model.DepartmentFullResponse;
import ru.digitalhabits.homework3.model.DepartmentRequest;
import ru.digitalhabits.homework3.model.DepartmentShortResponse;
import ru.digitalhabits.homework3.service.DepartmentService;
import ru.digitalhabits.homework3.service.PersonService;

import javax.persistence.EntityNotFoundException;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DepartmentController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepartmentControllerTest extends BaseRestControllerTest<DepartmentRequest, DepartmentShortResponse, DepartmentFullResponse> {

    static final int personId = 1;
    static final int departmentId = 1;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private PersonService personService;

    @BeforeAll
    void setup() {
        super.setup("department", this.departmentService, DepartmentRequest.class, DepartmentShortResponse.class, DepartmentFullResponse.class);
    }

    @Test
    void departments() throws Exception {
        super.getAll("departmentShortResponse1.json", "departmentShortResponse2.json");
    }

    @Test
    void department_existing_ok() throws Exception {
        super.get_existing_ok("departmentFullResponse.json");
    }

    @Test
    void createDepartment_valid_created() throws Exception {
        super.post_valid_created("departmentRequest.json");
    }

    @Test
    void createDepartment_nonValid_badRequest() throws Exception {
        super.post_nonValid_badRequest("departmentRequestNonValid.json", 1);
    }

    @Test
    void updateDepartment_existingAndValid_ok() throws Exception {
        super.patch_existingAndValid_ok("departmentRequest.json", "departmentFullResponse.json");
    }

    @Test
    void updateDepartment_nonValid_badRequest() throws Exception {
        super.patch_nonValid_badRequest("departmentRequestNonValid.json", 1);
    }

    @Test
    void updateDepartment_nonExistingValid_notFound() throws Exception {
        super.patch_nonExistingValid_notFound("departmentRequest.json");
    }

    @Test
    void addPersonToDepartment_bothExisting_noContent() throws Exception {
        this.mockMvc.perform(post(apiPath + "/{departmentId}/{personId}", departmentId, personId))
                .andExpect(status().isNoContent());
    }

    @Test
    void addPersonToDepartment_nonExistingPerson_notFound() throws Exception {
        String personNotFoundMessage = String.format(BaseDao.NOT_FOUND_MESSAGE_TEMPLATE, "Person");

        willThrow(new EntityNotFoundException(personNotFoundMessage))
                .given(personService)
                .addPersonToDepartment(departmentId, personId);
        this.mockMvc.perform(post(apiPath + "/{departmentId}/{personId}", departmentId, personId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(personNotFoundMessage));
    }

    @Test
    void addPersonToDepartment_nonExistingDepartment_notFound() throws Exception {
        willThrow(new EntityNotFoundException(notFoundMessage))
                .given(personService)
                .addPersonToDepartment(departmentId, personId);
        this.mockMvc.perform(post(apiPath + "/{departmentId}/{personId}", departmentId, personId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(notFoundMessage));
    }

    @Test
    void addPersonToDepartment_closedDepartment_conflict() throws Exception {
        String illegalStateMessage = "Person could not be added to closed department";

        willThrow(new IllegalStateException(illegalStateMessage))
                .given(personService)
                .addPersonToDepartment(departmentId, personId);
        this.mockMvc.perform(post(apiPath + "/{departmentId}/{personId}", departmentId, personId))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(illegalStateMessage));
    }

    @Test
    void removePersonFromDepartment_existing_noContent() throws Exception {
        this.mockMvc.perform(delete(apiPath + "/{departmentId}/{personId}", departmentId, personId))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePersonFromDepartment_nonExisting_notFound() throws Exception {
        willThrow(new EntityNotFoundException(notFoundMessage))
                .given(personService)
                .removePersonFromDepartment(departmentId, personId);
        this.mockMvc.perform(delete(apiPath + "/{departmentId}/{personId}", departmentId, personId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(notFoundMessage));
    }

    @Test
    void closeDepartment_existing_noContent() throws Exception {
        this.mockMvc.perform(post(apiPath + "/{id}/close", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void closeDepartment_nonExisting_notFound() throws Exception {
        willThrow(new EntityNotFoundException(notFoundMessage))
                .given(departmentService)
                .close(id);
        this.mockMvc.perform(post(apiPath + "/{id}/close", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(notFoundMessage));
    }
}
