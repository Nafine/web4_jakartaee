package se.ifmo.security.filter.auth;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import se.ifmo.security.JwtSecurityContext;
import se.ifmo.security.exception.AuthException;
import se.ifmo.security.model.UserPrincipal;
import se.ifmo.service.AuthService;


@Provider
@Priority(Priorities.AUTHENTICATION)
@AuthRequired
public class AuthFilter implements ContainerRequestFilter {
    @Inject
    private AuthService authService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        UserPrincipal user;

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        try {
            user = validate(authHeader);
        } catch (AuthException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
            return;
        }

        boolean isSecure = requestContext.getSecurityContext().isSecure();
        requestContext.setSecurityContext(new JwtSecurityContext(user, isSecure));
    }

    private UserPrincipal validate(String authHeader) throws AuthException {
        AuthHeader header = decodeHeader(authHeader);

        if (!header.schema().equals("Bearer")) {
            throw new AuthException("Invalid authentication schema");
        }

        return authService.validateAccessToken(header.value());
    }

    private AuthHeader decodeHeader(String header) throws AuthException {
        String[] splittedHeader = header.split(" ");

        if (splittedHeader.length < 2) {
            throw new AuthException("Invalid authentication header");
        }

        return new AuthHeader(splittedHeader[0], splittedHeader[1]);
    }

    record AuthHeader(String schema, String value) {
    }
}

