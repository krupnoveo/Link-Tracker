package edu.java.bot.models;

import edu.java.bot.api.dto.response.ApiErrorResponse;

public record GenericResponse<T>(T response, ApiErrorResponse errorResponse) {
}
