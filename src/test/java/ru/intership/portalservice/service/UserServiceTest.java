package ru.intership.portalservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.intership.portalservice.dto.ResetPasswordDto;
import ru.intership.portalservice.dto.UserDto;
import ru.intership.portalservice.dto.UserShortDto;
import ru.intership.portalservice.mapper.UserMapperImpl;
import ru.intership.portalservice.model.UserRole;
import ru.intership.portalservice.validator.UserValidator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private KeycloakService keycloakService;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private PasswordService passwordService;

    @Mock
    private MailService mailService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerCompanyOwner_ValidArgs() {
        String expectedUserId = UUID.randomUUID().toString();
        when(keycloakService.registerUser(any(UserRepresentation.class))).thenReturn(expectedUserId);
        when(passwordService.generate()).thenReturn("password");

        String actualUserId = userService.registerCompanyOwner(getUserDto());

        assertEquals(expectedUserId, actualUserId);
        verify(userMapper, times(1)).toUserRepresentation(any(UserDto.class));
        verify(keycloakService, times(1)).registerUser(any(UserRepresentation.class));
        verify(keycloakService, times(1)).assignRoleToUser(anyString(), anyString());
        verify(passwordService, times(1)).generate();
        verify(keycloakService, times(1)).addPasswordByUsername(anyString(), anyString());
        verify(mailService, times(1)).sendNewPasswordMail(anyString(), anyString());
    }

    @Test
    public void registerCompanyMember_UserIsNotExists() {
        String expectedUserId = UUID.randomUUID().toString();
        when(keycloakService.findUserId(anyString())).thenReturn(Optional.empty());
        when(keycloakService.registerUser(any(UserRepresentation.class))).thenReturn(expectedUserId);
        when(passwordService.generate()).thenReturn("password");
        when(keycloakService.findGroupId(anyString())).thenReturn(UUID.randomUUID().toString());
        when(keycloakService.isRoleExists(anyString())).thenReturn(true);

        String actualUserId = userService.registerCompanyMember(getCompanyInn(), getUserDto(), UserRole.LOGIST.name(), new HashSet<>());

        assertEquals(expectedUserId, actualUserId);
        verify(userValidator, times(1)).validateUserIsCompanyAdmin(anyString(), anySet());
        verify(userValidator, times(1)).validateUserAlreadyHaveRole(any(UserDto.class), anyString());
        verify(keycloakService, times(1)).findUserId(anyString());
        verify(keycloakService, times(1)).registerUser(any(UserRepresentation.class));
        verify(keycloakService, times(1)).addPasswordByUsername(anyString(), anyString());
        verify(mailService, times(1)).sendNewPasswordMail(anyString(), anyString());
        verify(keycloakService, times(2)).assignRoleToUser(anyString(), anyString());
    }

    @Test
    public void registerDriver_UserIsNotExists() {
        String expectedUserId = UUID.randomUUID().toString();
        when(keycloakService.findUserId(anyString())).thenReturn(Optional.empty());
        when(keycloakService.registerUser(any(UserRepresentation.class))).thenReturn(expectedUserId);
        when(passwordService.generate()).thenReturn("password");
        when(keycloakService.findGroupId(anyString())).thenReturn(UUID.randomUUID().toString());
        when(keycloakService.isRoleExists(anyString())).thenReturn(true);

        String actualUserId = userService.registerDriver(getCompanyInn(), getUserDto(), new HashSet<>());

        assertEquals(expectedUserId, actualUserId);
        verify(userValidator, times(1)).validateUserIsCompanyAdmin(anyString(), anySet());
        verify(userValidator, times(1)).validateUserAlreadyHaveRole(any(UserDto.class), anyString());
        verify(keycloakService, times(1)).findUserId(anyString());
        verify(keycloakService, times(1)).registerUser(any(UserRepresentation.class));
        verify(keycloakService, times(1)).addPasswordByUsername(anyString(), anyString());
        verify(mailService, times(1)).sendNewPasswordMail(anyString(), anyString());
        verify(keycloakService, times(2)).assignRoleToUser(anyString(), anyString());
    }

    @Test
    public void resetPassword_ValidArgs() {
        userService.resetPassword("", new ResetPasswordDto("whjw4Ek"));

        verify(keycloakService, times(1)).addPasswordByUsername(anyString(), anyString());
    }

    @Test
    public void updateUserInfo_ValidArgs() {
        UserDto expectedUserDto = getUserDto();

        UserDto actualUserDto = userService.updateUserInfo("", expectedUserDto);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    public void getUsersInCompany_ValidArgs() {
        List<UserShortDto> expectedUsersShortDtos = getUsersShortDtos();
        when(keycloakService.findGroupMembersByName(anyString())).thenReturn(getUsersRepresentations());
        when(keycloakService.getUserCompanyRoles(anyString(), anyString())).thenReturn(List.of());

        List<UserShortDto> actualUsersShortDtos = userService.getUsersInCompany(getCompanyInn(), Set.of());

        assertEquals(expectedUsersShortDtos, actualUsersShortDtos);
        verify(userValidator, times(1)).validateUserIsCompanyAdmin(anyString(), anySet());
        verify(keycloakService, times(1)).findGroupMembersByName(anyString());
        verify(keycloakService, times(expectedUsersShortDtos.size())).getUserCompanyRoles(anyString(), anyString());
    }

    @Test
    public void setAndSendPassword_ValidArgs() {
        String email = "example@example.ru";
        when(passwordService.generate()).thenReturn("password");

        userService.setAndSendPassword(email);

        verify(passwordService, times(1)).generate();
        verify(keycloakService, times(1)).addPasswordByUsername(anyString(), anyString());
        verify(mailService, times(1)).sendNewPasswordMail(anyString(), anyString());
    }

    private List<UserRepresentation> getUsersRepresentations() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName("Иван");
        userRepresentation.setLastName("Иванов");
        userRepresentation.setUsername("example@example.com");
        return List.of(userRepresentation);
    }

    private List<UserShortDto> getUsersShortDtos() {
        UserShortDto user = UserShortDto.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .username("example@example.com")
                .roles(List.of())
                .build();
        return List.of(user);
    }

    private String getCompanyInn() {
        return "1231245235153";
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .firstName("Пётр")
                .lastName("Иванов")
                .email("examle@example.ru")
                .build();
    }
}
