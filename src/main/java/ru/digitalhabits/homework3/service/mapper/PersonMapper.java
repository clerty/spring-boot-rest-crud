package ru.digitalhabits.homework3.service.mapper;

import org.mapstruct.*;
import ru.digitalhabits.homework3.domain.Person;
import ru.digitalhabits.homework3.model.PersonFullResponse;
import ru.digitalhabits.homework3.model.PersonRequest;
import ru.digitalhabits.homework3.model.PersonShortResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = DepartmentMapper.class
)
public interface PersonMapper {

    void personRequestToPerson(PersonRequest personRequest, @MappingTarget Person person);

    @Mapping(target = "fullName", source = "person", qualifiedByName = "getFullName")
    PersonShortResponse personToPersonShortResponse(Person person);

    @Mapping(target = "id", source = "person.id")
    @Mapping(target = "fullName", source = "person", qualifiedByName = "getFullName")
    PersonFullResponse personToPersonFullResponse(Person person);

    List<PersonShortResponse> personsListToPersonShortResponsesList(List<Person> persons);

    @Named("getFullName")
    default String getFullName(Person person) {
        return Stream.of(person.getFirstName(), person.getMiddleName(), person.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
