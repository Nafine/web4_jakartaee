package se.ifmo.api.dto.auth;

public record TokenResponse(String token, int expires) {
}
