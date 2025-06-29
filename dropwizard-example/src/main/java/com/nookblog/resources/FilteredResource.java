package com.nookblog.resources;

import com.nookblog.filter.DateRequired;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/filtered")
public class FilteredResource {

    @GET
    @DateRequired
    @Path("hello")
    public String sayHello() {
        return "hello";
    }
}
