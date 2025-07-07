package com.nookblog.core;

import java.security.Principal;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class User implements Principal {
    private final String name;
    private String email;
    private final Set<String> roles;
    private String password;
    public User(String name) {
        this.name = name;
        this.roles = null;
    }

    public User(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return UUID.randomUUID();
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }
    // TODO: fix password hashing
    public void setPassword(String password) {
        this.password = BCrypt.hash(password);
    }
    public String getPassword() {

    }
}
