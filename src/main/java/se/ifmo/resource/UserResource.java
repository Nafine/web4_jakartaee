package se.ifmo.resource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.apache.commons.codec.digest.DigestUtils;
import se.ifmo.api.request.UserRequest;
import se.ifmo.api.response.AuthResponse;
import se.ifmo.entity.User;
import se.ifmo.filter.auth.AuthRequired;
import se.ifmo.security.session.SessionStore;
import se.ifmo.service.db.UserService;

@Path("/api")
@ApplicationScoped
public class UserResource {
    private static final Gson gson = new Gson();
    @Inject
    private UserService userService;
    @Inject
    private SessionStore sessionStore;

    public UserResource() {
    }

    @POST
    @Path("register")
    @Consumes("application/json")
    public Response register(String userJson) {
        try {
            UserRequest user = gson.fromJson(userJson, UserRequest.class);
            if (user.getLogin() == null || user.getPassword() == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Provide all required fields")
                        .build();
            } else if (userService.getUserByName(user.getLogin()) != null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Such user already exists")
                        .build();
            }
            User newUser = new User();
            newUser.setName(user.getLogin());
            newUser.setPassword(user.getPassword());
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
        } catch (JsonSyntaxException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Invalid JSON")
                    .build();
        }
    }

    @Path("login")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response login(String userJson) {
        try {
            UserRequest user = gson.fromJson(userJson, UserRequest.class);
            User storedUser = userService.getUserByName(user.getLogin());
            if (storedUser != null && storedUser.getPassword().equals(
                    DigestUtils.sha256Hex(user.getPassword() + storedUser.getHash()))) {
                NewCookie cookie = new NewCookie.Builder("sessionid")
                        .value(sessionStore.generateSession(storedUser.getId()))
                        .maxAge(86400)
                        .httpOnly(true)
                        .path("/")
                        .build();
                return Response
                        .ok(gson.toJson(AuthResponse.success()))
                        .cookie(cookie)
                        .build();
            } else {
                return Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(gson.toJson(AuthResponse.success()))
                        .build();
            }
        } catch (JsonSyntaxException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Invalid JSON")
                    .build();
        }
    }

    @Path("login")
    @POST
    @Produces("application/json")
    @AuthRequired
    public Response checkToken() {
        return Response
                .ok(gson.toJson(AuthResponse.success()))
                .build();
    }

    @Path("logout")
    @POST
    @AuthRequired
    public void logout(@CookieParam("sessionid") Cookie cookie) {
        sessionStore.removeSession(cookie.getValue());
    }
}
