package com.nookblog.views;

import io.dropwizard.views.View;

public class LoginView extends View {
    private final String error;
    
    public LoginView(String error) {
        super("login.mustache");
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
}