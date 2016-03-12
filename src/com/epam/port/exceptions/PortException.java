package com.epam.port.exceptions;

public class PortException extends Exception{

    public PortException() {}

    public PortException(String message, Throwable exception) {
        super(message, exception);
    }

    public PortException(String message) {
        super(message);
    }

    public PortException(Throwable exception) {
        super(exception);
    }
}
