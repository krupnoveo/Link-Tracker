package edu.java.api.controllers;

import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.IncorrectRequestParametersException;
import edu.java.api.exceptions.InvalidUrlFormatException;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.api.exceptions.TooManyRequestsException;
import edu.java.api.exceptions.UnsupportedUrlException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({
        IncorrectRequestParametersException.class,
        MethodArgumentTypeMismatchException.class,
        MissingRequestHeaderException.class,
        HttpMessageNotReadableException.class
    })
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

    @ExceptionHandler({InvalidUrlFormatException.class, })
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

    @ExceptionHandler(UnsupportedUrlException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrorResponse unsupportedUrlHost(UnsupportedUrlException e) {
        return new ApiErrorResponse(
            "Неподдерживаемый хост",
            String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiErrorResponse tooManyRequests(TooManyRequestsException e) {
        return new ApiErrorResponse(
            "Превышен лимит запросов",
            String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse internalServerError(Exception e) {
        return new ApiErrorResponse(
            "внутренняя ошибка сервера",
            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            e.getClass().getName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
