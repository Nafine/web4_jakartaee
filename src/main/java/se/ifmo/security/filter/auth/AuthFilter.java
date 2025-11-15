package se.ifmo.security.filter.auth;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import se.ifmo.api.auth.AuthResponse;
import se.ifmo.security.session.SessionStore;

@Provider
@Priority(Priorities.AUTHENTICATION)
@AuthRequired
public class AuthFilter implements ContainerRequestFilter {
    @Inject
    private SessionStore sessionStore;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Cookie sessionCookie = requestContext.getCookies().get("sessionid");

        if (sessionCookie == null) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new AuthResponse(false))
                    .build());
            return;
        }

        String sessionId = sessionCookie.getValue();

        if (!sessionStore.isValid(sessionId)) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new AuthResponse(false))
                    .build());
        }
    }
}

