package com.nookblog;

import com.nookblog.auth.BasicAuthenticator;
import com.nookblog.auth.BasicAuthorizer;
import com.nookblog.core.User;
import com.nookblog.db.BlogDAO;
import com.nookblog.db.PostDAO;
import com.nookblog.db.UserDAO;
import com.nookblog.resources.*;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.views.common.ViewBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class NookApplication extends Application<NookConfiguration> {
    Logger logger = LoggerFactory.getLogger("logger");

    public static void main(String[] args) throws Exception {
        new NookApplication().run(args);
    }

    @Override
    public String getName() {
        return "nook-blog";
    }

    @Override
    public void initialize(Bootstrap<NookConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(NookConfiguration config) {
                logger.info("CONF: {}", config.getViewRendererConfiguration());

                return config.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(NookConfiguration conf, Environment env) {
        // Initialize DAOs (using in-memory storage for simplicity)
        final var userDAO = new UserDAO();
        final var blogDAO = new BlogDAO();
        final var postDAO = new PostDAO();

        createTestData(userDAO, blogDAO, postDAO);

        env.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new BasicAuthenticator(userDAO))
                        .setAuthorizer(new BasicAuthorizer())
                        .setRealm("Nook Blog")
                        .buildAuthFilter()));

        env.jersey().register(RolesAllowedDynamicFeature.class);
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        env.jersey().register(new HomeResource());
        env.jersey().register(new LoginResource(userDAO));
        env.jersey().register(new BlogResource(blogDAO, postDAO, userDAO));
        env.jersey().register(new PostResource(postDAO, blogDAO));
        env.jersey().register(new UserResource(userDAO, blogDAO));
    }

    private void createTestData(UserDAO userDAO, BlogDAO blogDAO, PostDAO postDAO) {
        User user1 = new User("john@example.com", "password123", "John Doe");
        User user2 = new User("jane@example.com", "password123", "Jane Smith");

        userDAO.create(user1);
        userDAO.create(user2);

        blogDAO.create(user1.getId(), "John's Tech Blog", "Thoughts on technology and programming");
        blogDAO.create(user2.getId(), "Jane's Travel Blog", "Adventures around the world");

        postDAO.create(1L, "Getting Started with Dropwizard",
                "Dropwizard is a fantastic framework for building RESTful web services...");
        postDAO.create(1L, "Java Best Practices",
                "Here are some important best practices when writing Java code...");
        postDAO.create(2L, "My Trip to Paris",
                "Paris is an amazing city with so much history and culture...");
    }
}
