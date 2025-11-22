package se.ifmo.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Size(min = 4, max = 20) @NotBlank String login,
        @Size(min = 4, max = 20) @NotBlank String password) {
}
