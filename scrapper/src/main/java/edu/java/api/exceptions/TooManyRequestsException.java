package edu.java.api.exceptions;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Превышен лимит запросов");
    }
}
