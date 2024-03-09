package edu.java.api.controllers;

import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.IncorrectRequestParametersException;
import edu.java.api.exceptions.InvalidUrlFormatException;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.api.exceptions.UnsupportedUrlHostException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IncorrectRequestParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse incorrectRequest(IncorrectRequestParametersException e) {
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(ChatDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse chatDoesNotExist(ChatDoesNotExistException e) {
        return new ApiErrorResponse(
            "Чат не существует",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse linkNotFound(LinkNotFoundException e) {
        return new ApiErrorResponse(
            "Ссылка не найдена",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(ChatAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse chatAlreadyRegistered(ChatAlreadyRegisteredException e) {
        return new ApiErrorResponse(
            "Чат уже зарегистрирован",
            String.valueOf(HttpStatus.CONFLICT.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(LinkAlreadyTrackedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse linkAlreadyTracked(LinkAlreadyTrackedException e) {
        return new ApiErrorResponse(
            "Ссылка уже отслеживается",
            String.valueOf(HttpStatus.CONFLICT.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(InvalidUrlFormatException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrorResponse invalidUrlFormat(InvalidUrlFormatException e) {
        return new ApiErrorResponse(
            "Неправильный формат ссылки",
            String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(UnsupportedUrlHostException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrorResponse unsupportedUrlHost(UnsupportedUrlHostException e) {
        return new ApiErrorResponse(
            "Неподдерживаемый хост",
            String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}