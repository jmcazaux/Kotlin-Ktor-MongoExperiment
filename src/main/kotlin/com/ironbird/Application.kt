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
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

internal val LOGGER = KtorSimpleLogger(Application::class.simpleName!!)

fun Application.module() {
    LOGGER.trace("Installing resources...")
    install(Resources)

    LOGGER.trace("Installing content negotiation...")
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    LOGGER.trace("Configuring API documentation...")
    configureApiDocumentation()

    LOGGER.trace("Configuring static routing...")
    configureStaticRouting()

    val authorRepository = getAuthorRepository()

    LOGGER.trace("Configuring routing...")
    routing {
        LOGGER.trace("Configuring \"/api/authors\" routes...")
        route("/api/authors") {
            configureAuthorsRouting(AuthorUseCasesImpl(authorRepository))
        }
    }
}
