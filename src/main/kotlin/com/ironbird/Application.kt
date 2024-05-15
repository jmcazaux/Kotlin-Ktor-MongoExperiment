package com.ironbird

import com.ironbird.domain.entity.Author
import com.ironbird.plugins.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Resources)
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()

    environment.monitor.subscribe(ServerReady) {
        log.info("Server started")
        log.warn("Starting experimentation...")

        val authorRepository = getAuthorRepository()
        val author = Author(firstName = "John", lastName = "Doe")
        runBlocking {
            val id = authorRepository.saveAuthor(author)
            log.warn("Author saved $id")
        }
    }
}
