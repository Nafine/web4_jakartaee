package se.ifmo.api.dto.dot;

import jakarta.validation.constraints.NotNull;

public record DotDto(
        @NotNull(message = "X cannot be null") Double x,
        @NotNull(message = "X cannot be null") Double y,
        @NotNull(message = "X cannot be null") Double r,
        Boolean result,
        @NotNull(message = "Timestamp cannot be null")
        Long timestamp,
        Long executionTime) {
}
