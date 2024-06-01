package ru.intership.portalservice.mapper;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.intership.portalservice.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "username", source = "email")
    UserRepresentation toUserRepresentation(UserDto userDto);

    UserDto toDto(UserRepresentation userRepresentation);
}
