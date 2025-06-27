package com.nookblog;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

public class NookConfiguration extends Configuration {

    @NotEmpty
    private String appName = "Nook Blog";

    @JsonProperty
    public String getAppName() {
        return appName;
    }

    @JsonProperty
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @JsonProperty("viewRendererConfiguration")
    private Map<String, Map<String, String>> viewRendererConfiguration = new HashMap<>();

    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }
}