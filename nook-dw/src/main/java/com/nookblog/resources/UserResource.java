package com.nookblog.resources;

import com.nookblog.core.Blog;
import com.nookblog.core.User;
import com.nookblog.db.BlogDAO;
import com.nookblog.db.UserDAO;
import com.nookblog.views.ProfileView;
import io.dropwizard.auth.Auth;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import javax.annotation.security.RolesAllowed;

@Path("/profile")
@Produces(MediaType.TEXT_HTML)
public class UserResource {

    private final BlogDAO blogDAO;
    
    public UserResource(UserDAO userDAO, BlogDAO blogDAO) {
        this.blogDAO = blogDAO;
    }
    
    @GET
    @RolesAllowed("USER")
    public ProfileView getProfile(@Auth User user) {
        Blog blog = blogDAO.findByUserId(user.getId());
        return new ProfileView(user, blog);
    }
}