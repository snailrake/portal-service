package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.dto.ResetPasswordDto;
import ru.intership.portalservice.dto.UserDto;
import ru.intership.portalservice.dto.UserShortDto;
import ru.intership.portalservice.mapper.UserMapper;
import ru.intership.portalservice.model.UserRole;
import ru.intership.portalservice.validator.UserValidator;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final MailService mailService;
    private final UserValidator userValidator;

    public String registerCompanyOwner(UserDto userDto) {
        UserRepresentation userRepresentation = userMapper.toUserRepresentation(userDto);
        String userId = keycloakService.registerUser(userRepresentation);
        log.info("Registered company owner: " + userId);
        keycloakService.assignRoleToUser(userRepresentation.getUsername(), UserRole.REGISTRATOR.name());
        setAndSendPassword(userRepresentation.getEmail());
        return userId;
    }

    public String registerCompanyMember(String companyInn, UserDto userDto, String role, Set<String> roles) {
        userValidator.validateUserIsCompanyAdmin(companyInn, roles);
        String userId = registerUserInCompany(companyInn, userDto);
        assignRoleToUser(companyInn, userDto.getEmail(), role);
        return userId;
    }

    public String registerDriver(String companyInn, UserDto userDto, Set<String> roles) {
        return registerCompanyMember(companyInn, userDto, UserRole.DRIVER.name(), roles);
    }

    public void resetPassword(String userName, ResetPasswordDto resetPasswordDto) {
        keycloakService.addPasswordByUsername(userName, resetPasswordDto.getPassword());
    }

    public UserDto updateUserInfo(String username, UserDto userDto) {
        keycloakService.updateUserInfo(username, userDto.getEmail(), userDto.getFirstName(), userDto.getLastName());
        return userDto;
    }

    public List<UserShortDto> getUsersInCompany(String companyId, Set<String> roles) {
        userValidator.validateUserIsCompanyAdmin(companyId, roles);
        List<UserRepresentation> users = keycloakService.findGroupMembersByName(companyId);
        return users.stream()
                .map(user -> UserShortDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .username(user.getUsername())
                        .roles(keycloakService.getUserCompanyRoles(companyId, user.getUsername()))
                        .build())
                .toList();
    }

    public void setAndSendPassword(String email) {
        String password = passwordService.generate();
        keycloakService.addPasswordByUsername(email, password);
        mailService.sendNewPasswordMail(email, password);
    }

    private String registerUserInCompany(String companyInn, UserDto userDto) {
        userValidator.validateUserAlreadyHaveRole(userDto, companyInn);
        String userId = keycloakService.findUserId(userDto.getEmail())
                .orElseGet(() -> {
                    String id = keycloakService.registerUser(userMapper.toUserRepresentation(userDto));
                    log.info("User {} registered in {} company", id, companyInn);
                    setAndSendPassword(userDto.getEmail());
                    return id;
                });
        addUserToCompany(companyInn, userDto.getEmail());
        return userId;
    }

    private void addUserToCompany(String companyInn, String username) {
        String companyId = keycloakService.findGroupId(companyInn);
        keycloakService.joinToGroup(companyId, username);
    }

    private void assignRoleToUser(String companyInn, String username, String role) {
        keycloakService.assignRoleToUser(username, UserRole.valueOf(role).name());
        if (!keycloakService.isRoleExists(companyInn + UserRole.valueOf(role).name())) {
            keycloakService.registerRole(companyInn + UserRole.valueOf(role).name());
        }
        keycloakService.assignRoleToUser(username, companyInn + UserRole.valueOf(role).name());
        log.info("User {} received {} role in {} company", username, role, companyInn);
    }
}
