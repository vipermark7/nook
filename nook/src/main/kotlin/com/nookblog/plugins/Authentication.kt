package com.nookblog.plugins;


import com.nookblog.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    val userService = UserService()

    install(Authentication) {
        basic("auth-basic") {
            realm = "Blog Application"
            validate { credentials ->
                val user = userService.findUserByUsername(credentials.name)
                if (user != null && userService.validatePassword(user, credentials.password)) {
                    UserIdPrincipal(user.id.value.toString())
                } else {
                    null
                }
            }
        }
    }
}

