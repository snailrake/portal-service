package ru.intership.portalservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.intership.portalservice.dto.UserDto;
import ru.intership.portalservice.exception.MemberRegisterException;
import ru.intership.portalservice.exception.NotEnoughRightsException;
import ru.intership.portalservice.model.UserRole;
import ru.intership.portalservice.service.KeycloakService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final KeycloakService keycloakService;

    public void validateUserIsCompanyAdmin(String companyInn, Set<String> roles) {
        if (!roles.contains(companyInn + UserRole.ADMIN.name())) {
            throw new NotEnoughRightsException("User is not a company admin");
        }
    }

    public void validateUserAlreadyHaveRole(UserDto userDto, String role) {
        if (keycloakService.isUserHaveSeveralRolePattern(userDto.getEmail(), role)) {
            throw new MemberRegisterException(String.format("User %s is already have role in this company", userDto.getFirstName() + " " + userDto.getLastName()));
        }
    }

    public void validateUserIsAdmin(Set<String> roles) {
        if (!roles.contains(UserRole.ADMIN.name())) {
            throw new NotEnoughRightsException("User is not admin");
        }
    }

    public void validateUserIsCompanyLogistOrAdmin(String companyInn, Set<String> roles) {
        if (!roles.contains(companyInn + UserRole.ADMIN.name()) && !roles.contains(companyInn + UserRole.LOGIST.name())) {
            throw new NotEnoughRightsException("User is not a company admin or logist");
        }
    }
}
