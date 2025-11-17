package se.ifmo.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDto(
        @NotNull @NotBlank String id,
        @Size(min = 4, max = 20) String name,
        @Size(min = 4, max = 20) String password) {
}
