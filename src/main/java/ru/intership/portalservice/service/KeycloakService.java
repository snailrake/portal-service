package ru.intership.portalservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import ru.intership.portalservice.config.KeycloakConfig;
import ru.intership.portalservice.exception.KeycloakEntityNotFoundException;
import ru.intership.portalservice.exception.UserRegistrationException;
import ru.intership.portalservice.model.UserRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class KeycloakService {

    private final RealmResource realm;
    private String clientId;

    @PostConstruct
    public void init() {
        clientId = realm.clients().findAll().stream()
                .filter(client -> client.getClientId().equals(clientId))
                .toList()
                .get(0)
                .getId();
        Arrays.stream(UserRole.values())
                .forEach(role -> {
                    if (!isRoleExists(role.name())) registerRole(role.name());
                });
    }

    public KeycloakService(Keycloak keycloakClient, KeycloakConfig keycloakConfig) {
        this.realm = keycloakClient.realm(keycloakConfig.getRealm());
        this.clientId = keycloakConfig.getClientId();
    }

    public String registerUser(UserRepresentation user) {
        user.setEnabled(true);
        Response response = realm.users().create(user);
        try (response) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return CreatedResponseUtil.getCreatedId(response);
            } else {
                throw new UserRegistrationException(String.format("Failed to register user: %s, with status %d", response.getStatusInfo().getReasonPhrase(), response.getStatus()));
            }
        }
    }

    public void assignRoleToUser(String username, String role) {
        UserRepresentation user = realm.users().search(username, true)
                .get(0);
        RoleRepresentation clientRole = realm.clients().get(clientId).roles().get(role).toRepresentation();
        UserResource userResource = realm.users().get(user.getId());
        userResource.roles().clientLevel(clientId).add(Collections.singletonList(clientRole));
    }

    public void assignRoleToGroup(String groupId, String role) {
        RoleRepresentation clientRole = realm.clients().get(clientId).roles().get(role).toRepresentation();
        realm.groups().group(groupId).roles().clientLevel(clientId).add(List.of(clientRole));
    }

    public void addPasswordByUsername(String username, String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        realm.users().searchByUsername(username, true).stream()
                .findFirst()
                .map(UserRepresentation::getId)
                .ifPresentOrElse(userId -> realm.users().get(userId).resetPassword(credentials),
                        () -> {
                            throw new KeycloakEntityNotFoundException(String.format("User %s not found", username));
                        });
    }

    public boolean isUserHaveSeveralRolePattern(String username, String rolePattern) {
        List<UserRepresentation> users = realm.users().search(username, true);
        if (users.isEmpty()) return false;
        UserRepresentation user = users.get(0);
        UserResource userResource = realm.users().get(user.getId());
        long cnt = userResource.roles().clientLevel(clientId).listAll().stream()
                .map(RoleRepresentation::getName)
                .filter(roleName -> roleName.startsWith(rolePattern)
                        && !roleName.equalsIgnoreCase(rolePattern + UserRole.ADMIN.name()))
                .count();
        return cnt > 1;
    }

    public List<String> getUserCompanyRoles(String companyInn, String username) {
        List<UserRepresentation> users = realm.users().search(username, true);
        if (users.isEmpty()) return Collections.emptyList();
        UserRepresentation user = users.get(0);
        UserResource userResource = realm.users().get(user.getId());
        return userResource.roles().clientLevel(clientId).listAll().stream()
                .map(RoleRepresentation::getName)
                .filter(role -> role.startsWith(companyInn))
                .map(role -> role.replace(companyInn, ""))
                .toList();
    }

    public String registerGroup(String name) {
        GroupRepresentation group = getGroupRepresentation(name);
        Response response = realm.groups().add(group);
        try (response) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return CreatedResponseUtil.getCreatedId(response);
            } else {
                throw new UserRegistrationException(String.format("Failed to register user: %s, with status %d", response.getStatusInfo().getReasonPhrase(), response.getStatus()));
            }
        }
    }

    public void joinToGroup(String groupId, String username) {
        UserRepresentation user = realm.users().search(username, true)
                .get(0);
        realm.users().get(user.getId()).joinGroup(groupId);
    }

    public void registerRole(String role) {
        realm.clients().get(clientId).roles().create(getRoleRepresentation(role));
    }

    public boolean isRoleExists(String role) {
        return !realm.clients().get(clientId).roles().list(role, true).isEmpty();
    }

    public String findGroupId(String group) {
        return realm.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(group))
                .findFirst()
                .orElseThrow(() -> new KeycloakEntityNotFoundException(String.format("Group %s does not exist", group)))
                .getId();
    }

    public boolean isGroupExists(String group) {
        List<GroupRepresentation> groups = realm.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(group))
                .toList();
        return !groups.isEmpty();
    }

    public Optional<String> findUserId(String username) {
        List<UserRepresentation> users = realm.users().search(username, true);
        if (users.isEmpty()) return Optional.empty();
        return Optional.ofNullable(users.get(0).getId());
    }

    public void updateUserInfo(String email, String newEmail, String newFirstName, String newLastName) {
        UserRepresentation user = findUserByUsername(email)
                .orElseThrow(() -> new KeycloakEntityNotFoundException(String.format("User %s not found", email)));
        user.setUsername(newEmail);
        user.setEmail(newEmail);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        UserResource userResource = findUserResourceById(user.getId())
                .orElseThrow(() -> new KeycloakEntityNotFoundException(String.format("User %s not found", user.getId())));
        userResource.update(user);
    }

    public List<UserRepresentation> findGroupMembersByName(String groupName) {
        Optional<GroupRepresentation> group = findGroupByName(groupName);
        if (group.isEmpty()) return Collections.emptyList();
        return getGroupResource(group.get()).members();
    }

    public List<UserRepresentation> findUsersByRole(String role) {
        RoleResource roleResource = realm.clients().get(clientId).roles().get(role);
        return roleResource.getUserMembers();
    }

    private Optional<GroupRepresentation> findGroupByName(String name) {
        List<GroupRepresentation> groups = realm.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .toList();
        return !groups.isEmpty() ? Optional.of(groups.get(0)) : Optional.empty();
    }

    private GroupResource getGroupResource(GroupRepresentation group) {
        return realm.groups().group(group.getId());
    }

    private Optional<UserResource> findUserResourceById(String userId) {
        return Optional.of(realm.users().get(userId));
    }

    private Optional<UserRepresentation> findUserByUsername(String username) {
        return realm.users().search(username, true).stream()
                .findFirst();
    }

    private GroupRepresentation getGroupRepresentation(String group) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(group);
        return groupRepresentation;
    }

    private RoleRepresentation getRoleRepresentation(String role) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role);
        return roleRepresentation;
    }
}
