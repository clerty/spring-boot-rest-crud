package ru.digitalhabits.homework3.service.mapper;

import org.mapstruct.*;
import ru.digitalhabits.homework3.domain.Department;
import ru.digitalhabits.homework3.model.DepartmentFullResponse;
import ru.digitalhabits.homework3.model.DepartmentRequest;
import ru.digitalhabits.homework3.model.DepartmentShortResponse;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = PersonMapper.class
)
public interface DepartmentMapper {

    void departmentRequestToDepartment(DepartmentRequest departmentRequest, @MappingTarget Department department);

    DepartmentShortResponse departmentToDepartmentShortResponse(Department department);

    DepartmentFullResponse departmentToDepartmentFullResponse(Department department);

    List<DepartmentShortResponse> departmentsListToDepartmentShortResponsesList(List<Department> departments);
}
