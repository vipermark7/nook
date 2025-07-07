package com.nookblog.views;

import io.dropwizard.views.common.View;

public class ErrorView extends View {

    public ErrorView(String message) {
        super("error.mustache");
    }
}