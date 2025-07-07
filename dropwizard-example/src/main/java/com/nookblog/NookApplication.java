package com.nookblog;

import com.nookblog.auth.ExampleAuthenticator;
import com.nookblog.auth.ExampleAuthorizer;
import com.nookblog.cli.RenderCommand;
import com.nookblog.core.Person;
import com.nookblog.core.Template;
import com.nookblog.core.User;
import com.nookblog.db.BlogDAO;
import com.nookblog.db.PersonDAO;
import com.nookblog.db.PostDAO;
import com.nookblog.db.UserDAO;
import com.nookblog.filter.DateRequiredFeature;
import com.nookblog.health.TemplateHealthCheck;
import com.nookblog.resources.*;
import com.nookblog.tasks.EchoTask;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.views.common.ViewBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.Map;

public class NookApplication extends Application<NookConfiguration> {
    public static void main(String[] args) throws Exception {
        new NookApplication().run(args);
    }

    private final HibernateBundle<NookConfiguration> hibernateBundle =
            new HibernateBundle<>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(NookConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "nookblog";
    }

    @Override
    public void initialize(Bootstrap<NookConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<>() {
            @Override
            public DataSourceFactory getDataSourceFactory(NookConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(NookConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
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

        final Template template = conf.buildTemplate();

        env.healthChecks().register("template", new TemplateHealthCheck(template));
        env.admin().addTask(new EchoTask());
        env.jersey().register(DateRequiredFeature.class);
        env.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ExampleAuthenticator())
                .setAuthorizer(new ExampleAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        env.jersey().register(RolesAllowedDynamicFeature.class);
        env.jersey().register(new HelloWorldResource(template));

        env.jersey().register(new ViewResource());
        env.jersey().register(new ProtectedResource());
        env.jersey().register(new FilteredResource());
        env.jersey().register(new HomeResource());
        env.jersey().register(new LoginResource(userDAO));
        env.jersey().register(new BlogResource(blogDAO, postDAO, userDAO));
        env.jersey().register(new PostResource(postDAO, blogDAO));
        env.jersey().register(new UserResource(blogDAO));
    }

    private void createTestData(UserDAO userDAO, BlogDAO blogDAO, PostDAO postDAO) {
        User user1 = new User("user1");
        User user2 = new User("user2");

        userDAO.create(user1);
        userDAO.create(user2);

        blogDAO.create((long) user1.getId(), "John's Tech Blog", "Thoughts on technology and programming");
        blogDAO.create((long) user2.getId(), "Jane's Travel Blog", "Adventures around the world");

        postDAO.create(1L, "Getting Started with Dropwizard",
                "Dropwizard is a fantastic framework for building RESTful web services...");
        postDAO.create(1L, "Java Best Practices",
                "Here are some important best practices when writing Java code...");
        postDAO.create(2L, "My Trip to Paris",
                "Paris is an amazing city with so much history and culture...");
    }
}
