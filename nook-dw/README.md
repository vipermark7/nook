# Blog Application

A simple blog application built with Dropwizard, featuring user authentication, one blog per user, and multiple posts per blog.

## Features

- **User Authentication**: Basic authentication with email/password
- **One Blog per User**: Each user can create exactly one blog
- **Multiple Posts**: Users can create unlimited posts in their blog
- **CRUD Operations**: Full create, read, update, delete functionality for blogs and posts
- **Clean UI**: Simple HTML interface with a working navbar on every page

## Project Structure

```
blog-application/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/nookblog/
│       │       ├── BlogApplication.java          # Main application class
│       │       ├── BlogConfiguration.java        # Configuration
│       │       ├── core/                         # Domain models
│       │       │   ├── User.java
│       │       │   ├── Blog.java
│       │       │   └── Post.java
│       │       ├── auth/                         # Authentication
│       │       │   ├── BasicAuthenticator.java
│       │       │   └── BasicAuthorizer.java
│       │       ├── db/                           # Data access
│       │       │   ├── UserDAO.java
│       │       │   ├── BlogDAO.java
│       │       │   └── PostDAO.java
│       │       ├── resources/                    # HTTP endpoints
│       │       │   ├── HomeResource.java
│       │       │   ├── LoginResource.java
│       │       │   ├── BlogResource.java
│       │       │   ├── PostResource.java
│       │       │   └── UserResource.java
│       │       └── views/                        # View models
│       │           ├── HomeView.java
│       │           ├── LoginView.java
│       │           ├── BlogListView.java
│       │           ├── BlogView.java
│       │           ├── BlogFormView.java
│       │           ├── PostFormView.java
│       │           ├── ProfileView.java
│       │           └── ErrorView.java
│       └── resources/
│           └── com/nookblog/views/           # Mustache templates
│               ├── base.mustache
│               ├── home.mustache
│               ├── login.mustache
│               ├── blog-list.mustache
│               ├── blog.mustache
│               ├── blog-form.mustache
│               ├── post-form.mustache
│               ├── profile.mustache
│               └── error.mustache
├── config.yml                                    # Application configuration
└── pom.xml                                       # Maven configuration
```

## Prerequisites

- Java 24 or higher
- Maven 3.6 or higher

## How to Run

1. **Clone the repository** (or create the file structure as shown above)

2. **Build the application**:
   ```bash
   mvn clean package
   ```

3. **Run the application**:
   ```bash
   java -jar target/nookblog-0.1.jar server config.yml
   ```

4. **Access the application**:
    - Main application: http://localhost:8080/
    - Admin interface: http://localhost:8081/

## Default Test Accounts

The application comes with two pre-configured test accounts:

- **John Doe**
    - Email: john@example.com
    - Password: password123
    - Has blog: "John's Tech Blog"

- **Jane Smith**
    - Email: jane@example.com
    - Password: password123
    - Has blog: "Jane's Travel Blog"

## Usage

1. **Landing Page** (`/`): Static welcome page with information about the application

2. **Login** (`/login`): Login with email and password

3. **All Blogs** (`/blogs`): View all blogs in the system (no authentication required)

4. **View Blog** (`/blogs/{id}`): View a specific blog and its posts

5. **Profile** (`/profile`): View your profile and access your blog (requires authentication)

6. **Create/Edit/Delete**: All create, edit, and delete operations require authentication

## Important Notes

- This application uses in-memory storage for simplicity. All data is lost when the application restarts.
- Authentication is implemented using HTTP Basic Auth. In production, you should use HTTPS and consider more secure authentication methods.
- Each user can have only one blog. Attempting to create a second blog will result in an error.
- The navbar is present on all pages and provides easy navigation throughout the application.

## Template File Locations

Make sure to place the Mustache templates in the correct directory:
```
src/main/resources/com/example/blog/views/
```

All templates should have the `.mustache` extension and should be in the exact path shown above for the view classes to find them.

## Development Tips

- The application uses Dropwizard's view bundle with Mustache templates
- All views extend the base template for consistent navbar and styling
- Authentication is handled via Basic Auth with the `@Auth` annotation
- DAOs use in-memory ConcurrentHashMap for thread-safe storage

## Extending the Application

To add persistence:
1. Add Dropwizard JDBI or Hibernate bundle
2. Replace the in-memory DAOs with database-backed implementations
3. Add database configuration to `config.yml`

To improve security:
1. Add password hashing (BCrypt recommended)
2. Implement session-based authentication
3. Add CSRF protection for forms
4. Use HTTPS in production