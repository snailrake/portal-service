package ru.intership.portalservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.intership.portalservice.exception.CompanyRegistrationException;
import ru.intership.portalservice.service.KeycloakService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyValidatorTest {

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private CompanyValidator companyValidator;

    @Test
    public void validateGroupNotExists_GroupExists_ThrowsCompanyRegistrationException() {
        when(keycloakService.isGroupExists(anyString())).thenReturn(true);

        assertThrows(CompanyRegistrationException.class, () -> companyValidator.validateGroupNotExists(""));
    }

    @Test
    public void validateGroupExists_GroupNotExists_DoesNotThrowException() {
        when(keycloakService.isGroupExists(anyString())).thenReturn(false);

        assertDoesNotThrow(() -> companyValidator.validateGroupNotExists(""));
    }
}
