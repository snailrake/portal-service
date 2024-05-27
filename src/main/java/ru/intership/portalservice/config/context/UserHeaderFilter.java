package ru.intership.portalservice.config.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("x-user-id");
        String userName = req.getHeader("x-user-name");
        String userRoles = req.getHeader("x-user-roles");
        if (userId != null) userContext.setUserId(userId);
        if (userName != null) userContext.setUserName(userName);
        if (userRoles != null) {
            userContext.setUserRoles(objectMapper.readValue(userRoles, new TypeReference<>() {
            }));
        }
        chain.doFilter(request, response);
    }
}
