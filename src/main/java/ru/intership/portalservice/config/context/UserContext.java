package ru.intership.portalservice.config.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Set;

@Getter
@Setter
@Component
@RequestScope
public class UserContext {

    private String userId;
    private String userName;
    private Set<String> userRoles;
}
