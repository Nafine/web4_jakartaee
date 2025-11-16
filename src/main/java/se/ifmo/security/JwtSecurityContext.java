package se.ifmo.security;

import jakarta.ws.rs.core.SecurityContext;
import se.ifmo.security.model.UserPrincipal;

import java.security.Principal;

public class JwtSecurityContext implements SecurityContext {
    private final UserPrincipal userPrincipal;
    private final boolean secure;

    public JwtSecurityContext(UserPrincipal userPrincipal, boolean secure) {
        this.userPrincipal = userPrincipal;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    @Override
    public boolean isUserInRole(String role) {
        if (role == null)
            return false;

        return userPrincipal.roles().contains(role);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }
}
