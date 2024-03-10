package edu.java.models;

import edu.java.api.dto.response.ApiErrorResponse;

public record GenericResponse<T>(T response, ApiErrorResponse errorResponse) {
}
