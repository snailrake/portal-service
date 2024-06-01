package ru.intership.portalservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.intership.portalservice.exception.*;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        return getErrorsMap(e);
    }

    private Map<String, String> getErrorsMap(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), String.format("Does not satisfy \"%s\" requirements", error.getField()))
                ));
    }
}
