package com.nookblog.auth;

import com.nookblog.core.User;
import io.dropwizard.auth.Authorizer;


public class BasicAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        // For this simple example, all authenticated users have the same role
        return "USER".equals(role);
    }
}