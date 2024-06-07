package com.ironbird.infrastructure.api

import com.ironbird.application.usecase.AuthorUseCases
import com.ironbird.commons.exceptions.DuplicateEntityException
import com.ironbird.domain.entity.Author
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class CreateAuthorSchema(val firstName: String, val lastName: String)

@Serializable
data class AuthorSchema(val id: String, val firstName: String, val lastName: String, val biography: String? = null) {
    constructor(author: Author) : this(author.id.toString(), author.firstName, author.lastName, author.biography)
}

@Serializable
data class UpdateAuthorSchema(val id: String, val biography: String)

@Suppress("detekt:TooGenericExceptionCaught")
fun Route.configureAuthorsRouting(authorUseCases: AuthorUseCases) {
    post {
        try {
            val authorRequest = call.receive<CreateAuthorSchema>()
            val author: Author = authorUseCases.createAuthor(authorRequest.firstName, authorRequest.lastName)
            call.respond(AuthorSchema(author))
        } catch (e: DuplicateEntityException) {
            call.respondText(e.message ?: "", status = HttpStatusCode.Conflict)
        } catch (e: Exception) {
            call.respondText(e.message ?: "", status = HttpStatusCode.InternalServerError)
        }
    }
}
