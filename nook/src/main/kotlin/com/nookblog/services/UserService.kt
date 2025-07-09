package com.nookblog.services;

import com.nookblog.models.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService {
    fun createUser(request: Article.CreateUserRequest): Article.UserDto = transaction {
        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

        val user = User.new {
            username = request.username
            email = request.email
            passwordHash = hashedPassword
        }

        user.toDto()
    }

    fun findUserByUsername(username: String): User? = transaction {
        User.find { Users.username eq username }.firstOrNull()
    }

    fun findUserById(id: Int): User? = transaction {
        User.findById(id)
    }

    fun validatePassword(user: User, password: String): Boolean {
        return BCrypt.checkpw(password, user.passwordHash)
    }

    fun getAllUsers(): List<Article.UserDto> = transaction {
        User.all().map {
            it.toDto()
        }
    }
}