package edu.java.bot.api.endpoints.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.api.exceptions.IncorrectRequestParametersException;
import edu.java.bot.api.exceptions.TooManyRequestsException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({IncorrectRequestParametersException.class, JsonParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse incorrectRequest(IncorrectRequestParametersException e) {
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "400",
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
}
