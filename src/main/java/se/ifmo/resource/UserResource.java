package se.ifmo.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.apache.commons.codec.digest.DigestUtils;
import se.ifmo.api.auth.AuthResponse;
import se.ifmo.api.auth.LoginRequest;
import se.ifmo.database.entity.User;
import se.ifmo.database.repository.UserRepository;
import se.ifmo.security.filter.auth.AuthRequired;
import se.ifmo.security.session.SessionStore;

@Path("/")
@ApplicationScoped
public class UserResource {
    @Inject
    private UserRepository userService;
    @Inject
    private SessionStore sessionStore;

    public UserResource() {
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid LoginRequest req) {
        if (userService.getUserByName(req.login()) != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Such user already exists")
                    .build();
        }
        User newUser = new User();
        newUser.setName(req.login());
        newUser.setPassword(req.password());
        userService.addUser(newUser);

        NewCookie cookie = new NewCookie.Builder("sessionid")
                .value(sessionStore.generateSession(newUser.getId()))
                .maxAge(86400)
                .path("/")
                .httpOnly(true)
                .build();
        return Response
                .status(Response.Status.CREATED)
                .cookie(cookie)
                .build();
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest req) {
        User storedUser = userService.getUserByName(req.login());
        if (storedUser != null && storedUser.getPassword().equals(
                DigestUtils.sha256Hex(req.password() + storedUser.getHash()))) {
            NewCookie cookie = new NewCookie.Builder("sessionid")
                    .value(sessionStore.generateSession(storedUser.getId()))
                    .maxAge(86400)
                    .httpOnly(true)
                    .path("/")
                    .build();
            return Response
                    .ok(new AuthResponse(true), MediaType.APPLICATION_JSON)
                    .cookie(cookie)
                    .build();
        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new AuthResponse(false))
                    .build();
        }
    }

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AuthRequired
    public AuthResponse checkToken() {
        return new AuthResponse(true);
    }

    @Path("logout")
    @POST
    @AuthRequired
    public void logout(@CookieParam("sessionid") Cookie cookie) {
        sessionStore.removeSession(cookie.getValue());
    }
}
