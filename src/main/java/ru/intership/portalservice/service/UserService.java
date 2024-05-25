package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.dto.ResetPasswordDto;
import ru.intership.portalservice.dto.UserDto;
import ru.intership.portalservice.dto.UserShortDto;
import ru.intership.portalservice.exception.MemberRegisterException;
import ru.intership.portalservice.exception.NotEnoughRightsException;
import ru.intership.portalservice.mapper.UserMapper;
import ru.intership.portalservice.model.UserRole;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final MailService mailService;

    public String registerCompanyOwner(UserDto userDto) {
        UserRepresentation userRepresentation = userMapper.toUserRepresentation(userDto);
        String userId = keycloakService.registerUser(userRepresentation);
        keycloakService.assignRoleToUser(userRepresentation.getUsername(), UserRole.REGISTRATOR.name());
        String password = setInitPassword(userDto.getEmail());
        mailService.sendNewPasswordMail(userRepresentation.getEmail(), password);
        return userId;
    }

    public String registerCompanyMember(String companyInn, UserDto userDto, String role, Set<String> roles) {
        validateUserIsCompanyAdmin(companyInn, roles);
        validateUserAlreadyHaveRole(userDto, companyInn);
        AtomicBoolean isUserExists = new AtomicBoolean(true);
        String userId = keycloakService.findUserId(userDto.getEmail())
                .orElseGet(() -> {
                    isUserExists.set(false);
                    return keycloakService.registerUser(userMapper.toUserRepresentation(userDto));
                });
        String companyId = keycloakService.findGroupId(companyInn);
        keycloakService.joinToGroup(companyId, userDto.getEmail());
        keycloakService.assignRoleToUser(userDto.getEmail(), UserRole.valueOf(role).name());
        if (!keycloakService.isRoleExists(companyInn + UserRole.valueOf(role).name())) {
            keycloakService.registerRole(companyInn + UserRole.valueOf(role).name());
        }
        keycloakService.assignRoleToUser(userDto.getEmail(), companyInn + UserRole.valueOf(role).name());
        if (!isUserExists.get()) {
            String password = setInitPassword(userDto.getEmail());
            mailService.sendNewPasswordMail(userDto.getEmail(), password);
        }
        return userId;
    }

    public void resetPassword(String userName, ResetPasswordDto resetPasswordDto) {
        keycloakService.addPasswordByUsername(userName, resetPasswordDto.getPassword());
    }

    public void regeneratePassword(String userName) {
        setInitPassword(userName);
    }

    public UserDto updateUserInfo(String username, UserDto userDto) {
        keycloakService.updateUserInfo(username, userDto.getEmail(), userDto.getFirstName(), userDto.getLastName());
        return userDto;
    }

    public String registerDriver(String companyInn, UserDto userDto, Set<String> roles) {
        validateUserIsCompanyLogistOrAdmin(companyInn, roles);
        validateUserAlreadyHaveRole(userDto, companyInn);
        AtomicBoolean isUserExists = new AtomicBoolean(true);
        String userId = keycloakService.findUserId(userDto.getEmail())
                .orElseGet(() -> {
                    isUserExists.set(false);
                    return keycloakService.registerUser(userMapper.toUserRepresentation(userDto));
                });
        String companyId = keycloakService.findGroupId(companyInn);
        keycloakService.joinToGroup(companyId, userDto.getEmail());
        keycloakService.assignRoleToUser(userDto.getEmail(), UserRole.DRIVER.name());
        if (!keycloakService.isRoleExists(companyInn + UserRole.DRIVER.name())) {
            keycloakService.registerRole(companyInn + UserRole.DRIVER.name());
        }
        keycloakService.assignRoleToUser(userDto.getEmail(), companyInn + UserRole.DRIVER.name());
        if (!isUserExists.get()) {
            String password = setInitPassword(userDto.getEmail());
            mailService.sendNewPasswordMail(userDto.getEmail(), password);
        }
        return userId;
    }

    public List<UserShortDto> getUsersInCompany(String companyId, Set<String> roles) {
        validateUserIsCompanyAdmin(companyId, roles);
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

    public void validateUserIsCompanyLogistOrAdmin(String companyInn, Set<String> roles) {
        if (!roles.contains(companyInn + UserRole.ADMIN.name()) && !roles.contains(companyInn + UserRole.LOGIST.name())) {
            throw new NotEnoughRightsException("User is not a company admin or logist");
        }
    }

    public void validateUserIsAdmin(Set<String> roles) {
        if (!roles.contains(UserRole.ADMIN.name())) {
            throw new NotEnoughRightsException("User is not admin");
        }
    }

    private void validateUserAlreadyHaveRole(UserDto userDto, String role) {
        if (keycloakService.isUserHaveSeveralRolePattern(userDto.getEmail(), role)) {
            throw new MemberRegisterException(String.format("User %s is already have role in this company", userDto.getFirstName() + " " + userDto.getLastName()));
        }
    }

    private void validateUserIsCompanyAdmin(String companyInn, Set<String> roles) {
        if (!roles.contains(companyInn + UserRole.ADMIN.name())) {
            throw new NotEnoughRightsException("User is not a company admin");
        }
    }

    private String setInitPassword(String username) {
        String password = passwordService.generate();
        keycloakService.addPasswordByUsername(username, password);
        return password;
    }
}
