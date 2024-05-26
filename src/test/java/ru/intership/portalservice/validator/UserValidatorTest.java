package ru.intership.portalservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.intership.portalservice.dto.UserDto;
import ru.intership.portalservice.exception.MemberRegisterException;
import ru.intership.portalservice.exception.NotEnoughRightsException;
import ru.intership.portalservice.model.UserRole;
import ru.intership.portalservice.service.KeycloakService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void validateUserIsCompanyAdmin_UserIsNotCompanyAdmin_ThrowsNotEnoughRightsException() {
        assertThrows(NotEnoughRightsException.class, () -> userValidator.validateUserIsCompanyAdmin("", new HashSet<>()));
    }

    @Test
    public void validateUserIsCompanyAdmin_UserIsCompanyAdmin_DoesNotThrowException() {
        assertDoesNotThrow(() -> userValidator.validateUserIsCompanyAdmin("", Set.of(UserRole.ADMIN.name())));
    }

    @Test
    public void validateUserAlreadyHaveRole_UserAlreadyHaveRole_ThrowsMemberRegisterException() {
        when(keycloakService.isUserHaveSeveralRolePattern(anyString(), anyString())).thenReturn(true);

        assertThrows(MemberRegisterException.class, () -> userValidator.validateUserAlreadyHaveRole(getUserDto(), ""));
    }

    @Test
    public void validateUserAlreadyHaveRole_UserDoesNotHaveRole_DoesNotThrowException() {
        when(keycloakService.isUserHaveSeveralRolePattern(anyString(), anyString())).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUserAlreadyHaveRole(getUserDto(), ""));
    }

    @Test
    public void validateUserIsAdmin_UserIsNotAdmin_ThrowsNotEnoughRightsException() {
        assertThrows(NotEnoughRightsException.class, () -> userValidator.validateUserIsAdmin(new HashSet<>()));
    }

    @Test
    public void validateUserIsAdmin_UserIsAdmin_DoesNotThrowException() {
        assertDoesNotThrow(() -> userValidator.validateUserIsAdmin(Set.of(UserRole.ADMIN.name())));
    }

    @Test
    public void validateUserIsCompanyLogistOrAdmin_UserIsNotCompanyLogistOrAdmin_ThrowsNotEnoughRightsException() {
        assertThrows(NotEnoughRightsException.class, () -> userValidator.validateUserIsCompanyLogistOrAdmin("", new HashSet<>()));
    }

    @Test
    public void validateUserIsCompanyLogistOrAdmin_UserIsCompanyLogist_DoesNotThrowException() {
        assertDoesNotThrow(() -> userValidator.validateUserIsCompanyLogistOrAdmin("", Set.of(UserRole.LOGIST.name())));
    }

    @Test
    public void validateUserIsCompanyLogistOrAdmin_UserIsCompanyAdmin_DoesNotThrowException() {
        assertDoesNotThrow(() -> userValidator.validateUserIsCompanyLogistOrAdmin("", Set.of(UserRole.ADMIN.name())));
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .email("example@example.ru")
                .build();
    }
}
