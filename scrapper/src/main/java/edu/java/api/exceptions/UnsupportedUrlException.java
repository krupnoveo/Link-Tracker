package edu.java.api.exceptions;

public class UnsupportedUrlException extends RuntimeException {
    public UnsupportedUrlException(String url) {
        super("Отслеживание ссылки %s на данный ресурс (часть ресурса) не поддерживается".formatted(url));
    }
}
