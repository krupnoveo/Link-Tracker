package edu.java.api.exceptions;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String url) {
        super("Ссылка %s не найдена".formatted(url));
    }
}
