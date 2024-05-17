package com.ironbird

import com.ironbird.application.usecase.AuthorUseCasesImpl
import com.ironbird.infrastructure.api.configureAuthorsRouting
import com.ironbird.infrastructure.data.getAuthorRepository
import com.ironbird.plugins.configureApiDocumentation
import com.ironbird.plugins.configureStaticRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Resources)
    install(ContentNegotiation) {
        json()
    }

    configureApiDocumentation()
    configureStaticRouting()

    val authorRepository = getAuthorRepository()

    routing {
        route("/authors") {
            configureAuthorsRouting(AuthorUseCasesImpl(authorRepository))
        }
    }
}
