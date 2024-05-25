package ru.intership.portalservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.intership.portalservice.exception.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CompanyRegistrationException.class)
    public ErrorResponse handleCompanyRegistrationException(CompanyRegistrationException e) {
        log.error("CompanyRegistrationException", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(KeycloakEntityNotFoundException.class)
    public ErrorResponse handleKeycloakEntityNotFoundException(KeycloakEntityNotFoundException e) {
        log.error("KeycloakEntityNotFoundException", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MemberRegisterException.class)
    public ErrorResponse handleMemberRegisterException(MemberRegisterException e) {
        log.error("MemberRegisterException", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotEnoughRightsException.class)
    public ErrorResponse handleNotEnoughRightsException(NotEnoughRightsException e) {
        log.error("NotEnoughRightsException", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserRegistrationException.class)
    public ErrorResponse handleUserRegistrationException(UserRegistrationException e) {
        log.error("UserRegistrationException", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }
}
