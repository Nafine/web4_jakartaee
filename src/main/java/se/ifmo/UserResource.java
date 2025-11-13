package se.ifmo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import se.ifmo.db.UserService;
import se.ifmo.entity.User;

import java.util.stream.Collectors;

@Path("/api")
@ApplicationScoped
public class UserResource {
    @Context
    private UriInfo context;

    @Inject
    private UserService userService;

    public UserResource() {
    }

    @GET
    @Produces("text/html")
    public String getHtml(){
        User user = new User();
        user.setName("admin");
        user.setPassword("admin");
        userService.saveUser(user);
        return "<html><body><h1>Hello, World!</h1></body></html>";
    }

    @Path("users")
    @GET
    @Produces("text/html")
    public String getUsers() {
        return userService.getAllUsers().stream().map(user ->
            "<div>" + user.getName() + " (" + user.getPassword() + ")</div>"
        ).collect(Collectors.joining("", "<html><body>", "</body></html>"));
    }
}
