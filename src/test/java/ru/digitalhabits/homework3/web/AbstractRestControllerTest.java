package ru.digitalhabits.homework3.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import ru.digitalhabits.homework3.TestUtils;
import ru.digitalhabits.homework3.dao.AbstractDao;
import ru.digitalhabits.homework3.service.CrudService;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public abstract class AbstractRestControllerTest<T, S, R> {

    private CrudService<T, S, R> crudService;
    private Class<T> requestClass;
    private Class<S> shortResponseClass;
    private Class<R> fullResponseClass;

    @Autowired
    protected MockMvc mockMvc;

    protected String apiPath;
    protected String notFoundMessage;
    protected Map<String, File> exampleFiles;

    protected void setup(String path, CrudService<T, S, R> crudService, Class<T> requestClass, Class<S> shortResponseClass, Class<R> fullResponseClass) {
        this.crudService = crudService;
        this.requestClass = requestClass;
        this.shortResponseClass = shortResponseClass;
        this.fullResponseClass = fullResponseClass;
        this.apiPath = String.format(TestUtils.API_PATH_TEMPLATE, path);
        this.notFoundMessage = String.format(AbstractDao.NOT_FOUND_MESSAGE_TEMPLATE, StringUtils.capitalize(path));
        this.exampleFiles = TestUtils.retrieveExampleFiles(path, "domain");
    }

    protected void getAll(String dtoFileName1, String dtoFileName2) throws Exception {
        S dto1 = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName1), shortResponseClass);
        S dto2 = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName2), shortResponseClass);

        given(this.crudService.findAll())
                .willReturn(List.of(dto1, dto2));
        this.mockMvc.perform(get(apiPath))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0]").value(dto1))
                .andExpect(jsonPath("$[1]").value(dto2));
    }

    @Test
    protected void getAll_runtimeException_internalServerError() throws Exception {
        String exceptionMessage = "RuntimeException";

        given(this.crudService.findAll())
                .willThrow(new RuntimeException(exceptionMessage));
        this.mockMvc.perform(get(apiPath))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(exceptionMessage));

    }

    protected void get_existing_ok(String dtoFileName) throws Exception {
        int id = 1;
        R dto = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName), fullResponseClass);

        given(this.crudService.getById(id))
                .willReturn(dto);
        this.mockMvc.perform(get(apiPath + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(dto));
    }

    @Test
    protected void get_nonExisting_notFound() throws Exception {
        int id = 1;

        given(this.crudService.getById(id))
                .willThrow(new EntityNotFoundException(notFoundMessage));
        this.mockMvc.perform(get(apiPath + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(notFoundMessage));
    }

    protected void post_valid_created(String dtoFileName) throws Exception {
        int id = 1;
        T dto = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName), requestClass);

        given(this.crudService.create(dto))
                .willReturn(id);
        this.mockMvc.perform(post(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.mapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(apiPath + "/" + id)));
    }

    protected void post_nonValid_badRequest(String dtoFileName, int errorsQty) throws Exception {
        T dto = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName), requestClass);

        this.mockMvc.perform(post(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.mapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Error on " + requestClass.getSimpleName())))
                .andExpect(jsonPath("$.errors", hasSize(errorsQty)));
    }

    protected void patch_existingAndValid_ok(String requestEntityFileName, String responseEntityFileName) throws Exception {
        int id = 1;
        T requestEntity = TestUtils.mapper.readValue(this.exampleFiles.get(requestEntityFileName), requestClass);
        R responseEntity = TestUtils.mapper.readValue(this.exampleFiles.get(responseEntityFileName), fullResponseClass);

        given(this.crudService.update(id, requestEntity))
                .willReturn(responseEntity);
        this.mockMvc.perform(patch(apiPath + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.mapper.writeValueAsString(requestEntity))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(responseEntity));
    }

    protected void patch_nonValid_badRequest(String dtoFileName, int errorsQty) throws Exception {
        int id = 1;
        T dto = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName), requestClass);

        this.mockMvc.perform(patch(apiPath + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.mapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Error on " + requestClass.getSimpleName())))
                .andExpect(jsonPath("$.errors", hasSize(errorsQty)));
    }

    protected void patch_nonExistingValid_notFound(String dtoFileName) throws Exception {
        int id = 1;
        T dto = TestUtils.mapper.readValue(this.exampleFiles.get(dtoFileName), requestClass);

        given(this.crudService.update(id, dto))
                .willThrow(new EntityNotFoundException(notFoundMessage));
        this.mockMvc.perform(patch(apiPath + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.mapper.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(notFoundMessage));
    }

    @Test
    protected void deleteHttpMethod() throws Exception {
        this.mockMvc.perform(delete(apiPath + "/" + 1))
                .andExpect(status().isNoContent());
    }
}
