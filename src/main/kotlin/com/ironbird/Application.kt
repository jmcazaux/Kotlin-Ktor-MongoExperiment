package com.ironbird

import com.ironbird.application.usecase.AuthorUseCasesImpl
import com.ironbird.infrastructure.api.configureAuthorsRouting
import com.ironbird.infrastructure.data.getAuthorRepository
import com.ironbird.plugins.configureApiDocumentation
import com.ironbird.plugins.configureStaticRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.logging.KtorSimpleLogger
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
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
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
