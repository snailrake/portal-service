package ru.intership.portalservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import ru.intership.common.UserContext;

@Configuration
public class UserContextConfig {

    @Bean
    @RequestScope
    public UserContext userContext() {
        return new UserContext();
    }
}
