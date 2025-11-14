package se.ifmo.resource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.apache.commons.codec.digest.DigestUtils;
import se.ifmo.api.AuthResponse;
import se.ifmo.db.UserService;
import se.ifmo.entity.User;
import se.ifmo.filter.auth.AuthRequired;
import se.ifmo.security.session.SessionStore;

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
    @Path("user")
    @Consumes("application/json")
    public Response registerUser(String userJson) {
        try {
            User user = gson.fromJson(userJson, User.class);
            if (user.getLogin() == null || user.getPassword() == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Provide all required fields")
                        .build();
            } else if (userService.getUserByLogin(user.getLogin()) != null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Such user already exists")
                        .build();
            }
            userService.addUser(user);
            NewCookie cookie = new NewCookie.Builder("sessionid")
                    .value(sessionStore.generateSession())
                    .maxAge(86400)
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

    @Path("auth")
    @POST
    @Produces("application/json")
    @AuthRequired
    public Response checkToken() {
        return Response
                .ok(gson.toJson(AuthResponse.success()))
                .build();
    }

    @Path("auth")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response authUser(String userJson, @Context Request req) {
        try {
            User user = gson.fromJson(userJson, User.class);
            User storedUser = userService.getUserByLogin(user.getLogin());
            if (storedUser != null && storedUser.getPassword().equals(
                    DigestUtils.sha256Hex(user.getPassword() + storedUser.getHash()))) {
                NewCookie cookie = new NewCookie.Builder("sessionid")
                        .value(sessionStore.generateSession())
                        .maxAge(86400)
                        .httpOnly(true)
                        .build();

                System.out.println("Success");
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
}
