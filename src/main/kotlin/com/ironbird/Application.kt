package com.ironbird

import com.ironbird.application.use_case.AuthorUseCases
import com.ironbird.infrastructure.api.configureAuthorRouting
import com.ironbird.infrastructure.data.getAuthorRepository
import com.ironbird.plugins.configureApiDocumentation
import com.ironbird.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Resources)
    configureApiDocumentation()
    configureRouting()

    val authorRepository = getAuthorRepository()

    routing {
        route("/authors") {
            configureAuthorRouting(AuthorUseCases(authorRepository))
        }
    }

//    environment.monitor.subscribe(ServerReady) {
//        log.info("Server started")
//        log.warn("Starting experimentation...")
//
//        val authorRepository = getAuthorRepository()
//        val author = Author(firstName = "John", lastName = "Doe")
//        runBlocking {
//            val id = authorRepository.saveAuthor(author)
//            log.warn("Author saved $id")
//        }
//    }
}
