package edu.java.api.exceptions;

public class ChatDoesNotExistException extends RuntimeException {
    public ChatDoesNotExistException(long id) {
        super("Чат %d не существует".formatted(id));
    }
}
