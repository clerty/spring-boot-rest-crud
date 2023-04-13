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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.digitalhabits.homework3.model.*;
import ru.digitalhabits.homework3.service.DepartmentService;
import ru.digitalhabits.homework3.service.PersonService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "Department REST API operations")
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final PersonService personService;

    @Operation(
            summary = "Get all departments",
            responses = @ApiResponse(responseCode = "200", description = "Departments", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DepartmentFullResponse.class))))
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DepartmentShortResponse> departments() {
        return departmentService.findAll();
    }

    @Operation(
            summary = "Get department by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Department for requested ID", content = @Content(schema = @Schema(implementation = DepartmentFullResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DepartmentFullResponse department(@PathVariable Integer id) {
        return departmentService.getById(id);
    }


    @Operation(
            summary = "Create new department",
            responses = {
                    @ApiResponse(responseCode = "201", description = "New department is created"),
                    @ApiResponse(responseCode = "400", description = "Wrong request format", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        final Integer id = departmentService.create(request);
        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @Operation(
            summary = "Update existing department",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Department for requested ID is updated", content = @Content(schema = @Schema(implementation = DepartmentFullResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong request format", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DepartmentFullResponse updateDepartment(@PathVariable Integer id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @Operation(
            summary = "Remove department for ID",
            responses = @ApiResponse(responseCode = "204", description = "Department for requested ID is removed")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable Integer id) {
        departmentService.delete(id);
    }

    @Operation(
            summary = "Add person to department",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Person added to department"),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Person could not be added to closed department", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{departmentId}/{personId}")
    public void addPersonToDepartment(@PathVariable Integer departmentId, @PathVariable Integer personId) {
        personService.addPersonToDepartment(departmentId, personId);
    }

    @Operation(
            summary = "Remove person from department",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Person is removed from department"),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{departmentId}/{personId}")
    public void removePersonFromDepartment(@PathVariable Integer departmentId, @PathVariable Integer personId) {
        personService.removePersonFromDepartment(departmentId, personId);
    }

    @Operation(
            summary = "Close department (not allowed to add new persons) and remove all existing persons from it",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Department is closed"),
                    @ApiResponse(responseCode = "404", description = "Requested data not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/close")
    public void closeDepartment(@PathVariable Integer id) {
        departmentService.close(id);
    }
}
