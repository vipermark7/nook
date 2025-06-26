package com.nookblog.resources;

import com.nookblog.core.User;
import com.nookblog.db.UserDAO;
import com.nookblog.views.LoginView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Base64;

@Path("/login")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {
    
    private final UserDAO userDAO;
    
    public LoginResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    @GET
    public LoginView getLoginPage() {
        return new LoginView(null);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password) {
        User user = userDAO.findByEmail(email);
        
        if (user != null && user.getPassword().equals(password)) {
            // Create basic auth header
            String credentials = email + ":" + password;
            // TODO: replace this with an ectual encryption method (bcrypt?)
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            
            return Response.seeOther(URI.create("/blogs"))
                    .header("Authorization", "Basic " + encodedCredentials)
                    .build();
        } else {
            return Response.ok(new LoginView("Invalid email or password")).build();
        }
    }
}