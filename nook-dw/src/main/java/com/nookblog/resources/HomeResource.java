package com.nookblog.resources;

import com.nookblog.views.HomeView;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {

    @GET
    public HomeView getHome() {
        return new HomeView();
    }
}