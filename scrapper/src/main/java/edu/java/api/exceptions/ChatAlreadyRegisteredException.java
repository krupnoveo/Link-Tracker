package edu.java.api.exceptions;

public class ChatAlreadyRegisteredException extends RuntimeException {
    public ChatAlreadyRegisteredException(long id) {
        super("Чат %d уже зарегистрирован".formatted(id));
    }
}
