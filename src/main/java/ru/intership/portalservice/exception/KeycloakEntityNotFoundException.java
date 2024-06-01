package ru.intership.portalservice.exception;

public class KeycloakEntityNotFoundException extends RuntimeException {

    public KeycloakEntityNotFoundException(String message) {
        super(message);
    }
}
