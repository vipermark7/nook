package com.nookblog.auth;

import com.nookblog.core.User;
import io.dropwizard.auth.AuthorizationContext;
import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;


public class BasicAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(User user, String s, @Nullable ContainerRequestContext containerRequestContext) {
        return false;
    }

    @Override
    public AuthorizationContext<User> getAuthorizationContext(User principal, String role, @Nullable ContainerRequestContext requestContext) {
        return Authorizer.super.getAuthorizationContext(principal, role, requestContext);
    }
}