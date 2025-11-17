package se.ifmo.api.dto.dot;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record DotDto(
        @NotNull(message = "X cannot be null") Double x,
        @NotNull(message = "X cannot be null") Double y,
        @NotNull(message = "X cannot be null") Double r,
        Boolean result,
        @PastOrPresent
        @NotNull(message = "Timestamp cannot be null")
        Instant timestamp,
        Long executionTime) {
}
