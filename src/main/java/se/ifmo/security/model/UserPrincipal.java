package se.ifmo.security.model;

import lombok.Singular;

import java.security.Principal;
import java.util.Set;

public record UserPrincipal(String id, @Singular Set<String> roles) implements Principal {
    @Override
    public String getName() {
        return id;
    }
}
