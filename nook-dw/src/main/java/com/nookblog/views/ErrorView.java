package com.nookblog.views;

import io.dropwizard.views.common.View;

public class ErrorView extends View {
    private final String message;
    
    public ErrorView(String message) {
        super("error.mustache");
        this.message = message;
    }
}