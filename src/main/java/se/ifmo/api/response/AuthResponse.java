package se.ifmo.api.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {
    private boolean success;

    public static AuthResponse success() {
        return new AuthResponse(true);
    }

    public static AuthResponse fail() {
        return new AuthResponse(false);
    }
}
