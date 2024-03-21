package edu.java.api.exceptions;

public class IncorrectRequestParametersException extends RuntimeException {
    public IncorrectRequestParametersException() {
        super("Некорректные параметры запроса");
    }
}
