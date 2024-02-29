package edu.java.api.exceptions;

public class InvalidUrlFormatException extends RuntimeException {
    public InvalidUrlFormatException(String url) {
        super("Ссылка %s имеет неправильный формат".formatted(url));
    }
}
