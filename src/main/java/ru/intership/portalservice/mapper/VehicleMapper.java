package ru.intership.portalservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.intership.portalservice.dto.VehicleDto;
import ru.intership.portalservice.model.Vehicle;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "companyInn", source = "company.inn")
    VehicleDto toDto(Vehicle vehicle);

    Vehicle toEntity(VehicleDto vehicleDto);
}
