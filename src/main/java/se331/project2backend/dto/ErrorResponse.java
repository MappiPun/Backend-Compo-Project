package se331.project2backend.dto;

import java.util.Map;

public record ErrorResponse(String message, Map<String, String> errors) {
    public ErrorResponse(String message) {
        this(message, null);
    }
}
