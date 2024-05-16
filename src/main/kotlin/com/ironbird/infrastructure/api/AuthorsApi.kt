package com.ironbird.infrastructure.api

import com.ironbird.application.use_case.AuthorUseCases
import com.ironbird.domain.entity.Author
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateAuthorSchema(val firstName: String, val lastName: String)

@Serializable
data class AuthorSchema(val id: String, val firstName: String, val lastName: String, val biography: String? = null) {
    constructor(author: Author) : this(author.id.toString(), author.firstName, author.lastName, author.biography)
}

@Serializable
data class UpdateAuthorSchema(val id: String, val biography: String)

fun Route.configureAuthorRouting(authorUseCases: AuthorUseCases)
{
    post {
        val authorRequest = call.receive(CreateAuthorSchema::class)
        val author: Author = authorUseCases.createAuthor(authorRequest.firstName, authorRequest.lastName)
            ?: return@post call.respond("Error creating author")
        call.respond(AuthorSchema(author))
    }
}