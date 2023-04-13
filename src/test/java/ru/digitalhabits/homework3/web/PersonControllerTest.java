package ru.digitalhabits.homework3.web;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.digitalhabits.homework3.model.PersonFullResponse;
import ru.digitalhabits.homework3.model.PersonRequest;
import ru.digitalhabits.homework3.model.PersonShortResponse;
import ru.digitalhabits.homework3.service.PersonService;

@WebMvcTest(controllers = PersonController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonControllerTest extends BaseRestControllerTest<PersonRequest, PersonShortResponse, PersonFullResponse> {

    @MockBean
    private PersonService personService;

    @BeforeAll
    void setup() {
        super.setup("person", personService, PersonRequest.class, PersonShortResponse.class, PersonFullResponse.class);
    }

    @Test
    void persons() throws Exception {
        super.getAll("personShortResponse1.json", "personShortResponse2.json");
    }

    @Test
    void person_existing_ok() throws Exception {
        super.get_existing_ok("personFullResponse.json");
    }

    @Test
    void createPerson_valid_created() throws Exception {
        super.post_valid_created("personRequest.json");
    }

    @Test
    void createPerson_nonValid_badRequest() throws Exception {
        super.post_nonValid_badRequest("personRequestNonValid.json", 2);
    }

    @Test
    void updatePerson_existingAndValid_ok() throws Exception {
        super.patch_existingAndValid_ok("personRequest.json", "personFullResponse.json");
    }

    @Test
    void updatePerson_nonValid_badRequest() throws Exception {
        super.patch_nonValid_badRequest("personRequestNonValid.json", 1);
    }

    @Test
    void updatePerson_nonExistingValid_notFound() throws Exception {
        super.patch_nonExistingValid_notFound("personRequest.json");
    }
}
