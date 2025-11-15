package se.ifmo.filter.auth;

import com.google.gson.Gson;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import se.ifmo.api.response.AuthResponse;
import se.ifmo.security.session.SessionStore;

@Provider
@Priority(Priorities.AUTHENTICATION)
@AuthRequired
public class AuthFilter implements ContainerRequestFilter {
    private static final Gson gson = new Gson();

    @Inject
    private SessionStore sessionStore;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Cookie sessionCookie = requestContext.getCookies().get("sessionid");

        if (sessionCookie == null) {
            requestContext.abortWith(Response
                            .status(Response.Status.UNAUTHORIZED)
                            .entity(gson.toJson(AuthResponse.fail()))
                            .build());
            return;
        }

        String sessionId = sessionCookie.getValue();

        if (!sessionStore.isValid(sessionId)) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(gson.toJson(AuthResponse.fail()))
                    .build());
        }
    }
}

