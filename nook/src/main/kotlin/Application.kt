package com.nookblog

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureTemplating()
    configureDatabases()
    configureAdministration()
    configureRouting()
}
