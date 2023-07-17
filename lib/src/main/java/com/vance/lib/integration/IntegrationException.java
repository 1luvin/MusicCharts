package com.vance.lib.integration;

public class IntegrationException extends Exception {

    public IntegrationException(String message, Exception exception) {
        super(message, exception);
    }

    public IntegrationException(String message) {
        super(message);
    }
}
