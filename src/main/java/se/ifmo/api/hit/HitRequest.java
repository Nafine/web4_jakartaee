package se.ifmo.api.hit;

import jakarta.validation.constraints.NotBlank;

public record HitRequest(
        @NotBlank Double x,
        @NotBlank Double y,
        @NotBlank Double r) {
}
