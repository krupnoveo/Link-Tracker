package edu.java.api.exceptions;

public class UnsupportedUrlHostException extends RuntimeException {
    public UnsupportedUrlHostException(String url) {
        super("Отслеживание ссылки %s на данный ресурс не поддерживается".formatted(url));
    }
}
