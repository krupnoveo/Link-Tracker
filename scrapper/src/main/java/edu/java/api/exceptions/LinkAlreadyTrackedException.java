package edu.java.api.exceptions;

public class LinkAlreadyTrackedException extends RuntimeException {
    public LinkAlreadyTrackedException(String url) {
        super("Ссылка %s уже отслеживается".formatted(url));
    }
}
