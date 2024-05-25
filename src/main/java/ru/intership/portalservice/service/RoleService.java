package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.model.UserRole;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    public List<String> getAllRoles() {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .filter(name -> !name.equalsIgnoreCase("REGISTRATOR"))
                .toList();
    }
}
