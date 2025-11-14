package se.ifmo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.codec.digest.DigestUtils;
import se.ifmo.api.AuthResponse;
import se.ifmo.db.UserService;
import se.ifmo.entity.User;

import java.util.List;

@Path("/api")
@ApplicationScoped
public class UserResource {
    private static final Gson gson = new Gson();
    private static final Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Context
    private UriInfo context;
    @Inject
    private UserService userService;

    public UserResource() {
    }

    @POST
    @Path("user")
    @Consumes("application/json")
    public Response addUser(String userJson) {
        try {
            User user = gson.fromJson(userJson, User.class);
            if (user.getLogin() == null || user.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Provide all required fields").build();
            }
            userService.addUser(user);
            return Response.status(Response.Status.OK).build();
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid JSON").build();
        }
    }

    @GET
    @Path("user/{username}")
    @Produces("application/json")
    public Response getUser(@PathParam("username") String username) {
        User user = userService.getUserByLogin(username);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String json = gson.toJson(user);
        return Response.ok(json).build();
    }

    @Path("users")
    @GET
    @Produces("text/html")
    public String getUsers() {
        List<User> users = userService.getAllUsers();
        return String.format("{users: %s}", gson.toJson(users));
    }

    @Path("auth")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response authUser(String userJson) {
        try {
            User user = gson.fromJson(userJson, User.class);
            User storedUser = userService.getUserByLogin(user.getLogin());
            System.out.println(user);
            System.out.println(storedUser);
            if (storedUser != null && storedUser.getPassword().equals(
                    DigestUtils.sha256Hex(user.getPassword() + storedUser.getHash()))) {
                AuthResponse resp = new AuthResponse(true);
                return Response.status(Response.Status.OK).entity(gson.toJson(resp)).build();
            } else {
                AuthResponse resp = new AuthResponse(false);
                return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(resp)).build();
            }
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid JSON").build();
        }
    }

    @Path("clear")
    @DELETE
    public void clearUsers() {
        userService.clearUsers();
    }
}
