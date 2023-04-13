package ru.digitalhabits.homework3.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.digitalhabits.homework3.model.*;
import ru.digitalhabits.homework3.model.validation.group.Create;
import ru.digitalhabits.homework3.service.PersonService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "Person REST API operations")
@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @Operation(
            summary = "Get all persons",
            responses = @ApiResponse(responseCode = "200", description = "Persons", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PersonShortResponse.class))))
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonShortResponse> persons() {
        return personService.findAll();
    }

    @Operation(
            summary = "Get person by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Person for requested ID", content = @Content(schema = @Schema(implementation = PersonFullResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonFullResponse person(@PathVariable Integer id) {
        return personService.getById(id);
    }

    @Operation(
            summary = "Create new person",
            responses = {
                    @ApiResponse(responseCode = "201", description = "New person is created"),
                    @ApiResponse(responseCode = "400", description = "Wrong request format", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createPerson(@Validated(Create.class) @RequestBody PersonRequest request) {
        final int id = personService.create(request);
        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @Operation(
            summary = "Update existing person",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Person for requested ID is updated", content = @Content(schema = @Schema(implementation = PersonFullResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong request format", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonFullResponse updatePerson(@PathVariable Integer id, @Valid @RequestBody PersonRequest request) {
        return personService.update(id, request);
    }

    @Operation(
            summary = "Remove person for ID",
            responses = @ApiResponse(responseCode = "204", description = "Person for requested ID is removed")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Integer id) {
        personService.delete(id);
    }
}
