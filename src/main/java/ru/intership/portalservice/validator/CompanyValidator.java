package ru.intership.portalservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.intership.portalservice.exception.CompanyRegistrationException;
import ru.intership.portalservice.service.KeycloakService;

@Component
@RequiredArgsConstructor
public class CompanyValidator {

    private final KeycloakService keycloakService;

    public void validateGroupNotExists(String companyInn) {
        if (keycloakService.isGroupExists(companyInn)) {
            throw new CompanyRegistrationException(String.format("Company %s already in registry", companyInn));
        }
    }
}
