package com.nookblog.resources;

import com.nookblog.core.Blog;
import com.nookblog.core.Post;
import com.nookblog.core.User;
import com.nookblog.db.BlogDAO;
import com.nookblog.db.PostDAO;
import com.nookblog.views.PostFormView;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.common.View;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/posts")
@Produces(MediaType.TEXT_HTML)
public class PostResource {
    
    private final PostDAO postDAO;
    private final BlogDAO blogDAO;
    
    public PostResource(PostDAO postDAO, BlogDAO blogDAO) {
        this.postDAO = postDAO;
        this.blogDAO = blogDAO;
    }
    
    @GET
    @Path("/new")
    @RolesAllowed("USER")
    public View newPostForm(@Auth User user, @QueryParam("blogId") Long blogId) {
        Blog blog = blogDAO.findById(blogId);
        if (blog == null) {
            throw new WebApplicationException(404);
        }
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        return new PostFormView(null, blogId);
    }
    
    @POST
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createPost(@Auth User user,
                               @FormParam("blogId") Long blogId,
                               @FormParam("title") String title,
                               @FormParam("content") String content) {
        Blog blog = blogDAO.findById(blogId);
        if (blog == null) {
            throw new WebApplicationException(404);
        }
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        
        Post post = postDAO.create(blogId, title, content);
        return Response.seeOther(URI.create("/blogs/" + blogId)).build();
    }
    
    @GET
    @Path("/{id}/edit")
    @RolesAllowed("USER")
    public View editPostForm(@Auth User user, @PathParam("id") Long id) {
        Post post = postDAO.findById(id);
        if (post == null) {
            throw new WebApplicationException(404);
        }
        
        Blog blog = blogDAO.findById(post.getBlogId());
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        
        return new PostFormView(post, post.getBlogId());
    }
    
    @POST
    @Path("/{id}/edit")
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updatePost(@Auth User user,
                              @PathParam("id") Long id,
                              @FormParam("title") String title,
                              @FormParam("content") String content) {
        Post post = postDAO.findById(id);
        if (post == null) {
            throw new WebApplicationException(404);
        }
        
        Blog blog = blogDAO.findById(post.getBlogId());
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        
        post.setTitle(title);
        post.setContent(content);
        postDAO.update(post);
        
        return Response.seeOther(URI.create("/blogs/" + post.getBlogId())).build();
    }
    
    @POST
    @Path("/{id}/delete")
    @RolesAllowed("USER")
    public Response deletePost(@Auth User user, @PathParam("id") Long id) {
        Post post = postDAO.findById(id);
        if (post == null) {
            throw new WebApplicationException(404);
        }
        
        Blog blog = blogDAO.findById(post.getBlogId());
        if (!blog.getUserId().equals(user.getId())) {
            throw new WebApplicationException(403);
        }
        
        Long blogId = post.getBlogId();
        postDAO.delete(id);
        
        return Response.seeOther(URI.create("/blogs/" + blogId)).build();
    }
}