package com.nookblog;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import javax.validation.constraints.NotEmpty;

public class BlogConfiguration extends Configuration {

    @NotEmpty
    private String appName = "Blog Application";

    @JsonProperty
    public String getAppName() {
        return appName;
    }

    @JsonProperty
    public void setAppName(String appName) {
        this.appName = appName;
    }
}