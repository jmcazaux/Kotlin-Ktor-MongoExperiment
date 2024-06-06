package com.ironbird.infrastructure.api

import com.ironbird.application.usecase.AuthorUseCases
import com.ironbird.commons.exceptions.DataWriteException
import com.ironbird.commons.exceptions.DuplicateEntityException
import com.ironbird.domain.entity.Author
import com.mongodb.MongoClientException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


private fun ApplicationTestBuilder.configureApplicationAndCreateClient(authorUseCases: AuthorUseCases): HttpClient {

    // We create a tuned application environment where we do not want the default modules
    environment {
        config = ApplicationConfig("")
    }

    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    // We configure the author's routing with the provided use case (mocked or not)
    routing {
        route("/api/authors") {
            configureAuthorsRouting(authorUseCases)
        }
    }

    // and we create a client to be used in the tests
    val client = createClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
    return client
}


private const val JET = "Jet"
private const val BRAINS = "Brains"

class AuthorsApiTest {

    @Test
    fun `POST on authors calls createAuthor and returns the author`() {

        // GIVEN an application created with a mocked use case
        val authorUseCases = mockk<AuthorUseCases>()
        coEvery {
            authorUseCases.createAuthor(any<String>(), any<String>())
        } answers {
            Author(firstName = args[0] as String, lastName = args[1] as String)
        }

        testApplication {
            val client = configureApplicationAndCreateClient(authorUseCases)

            // WHEN I post an author on the /api/authors endpoint
            val response = client.post("/api/authors") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(CreateAuthorSchema(JET, BRAINS))
            }

            // THEN I get an HTTP 200
            response.status shouldBe HttpStatusCode.OK

            // AND the createAuthor method is called on the use case
            coVerify {
                authorUseCases.createAuthor(firstName = JET, lastName = BRAINS)
            }

            // AND the author is returned
            val returnedAuthor = Json.decodeFromString<AuthorSchema>(response.bodyAsText())
            returnedAuthor.firstName shouldBe JET
            returnedAuthor.lastName shouldBe BRAINS
            returnedAuthor.id shouldNotBe null
        }
    }

    @Test
    fun `POST on authors returns HTTP 409 Conflict when the use case throws DuplicateEntityException`() {
        // GIVEN An application configured with a mocked use case
        val authorUseCases = mockk<AuthorUseCases>()
        coEvery {
            authorUseCases.createAuthor(any<String>(), any<String>())
        } throws (DuplicateEntityException("DUPLICATE_ENTITY_MESSAGE"))

        testApplication {
            val client = configureApplicationAndCreateClient(authorUseCases)
            // WHEN I post on /api/authors with an author payload
            val response = client.post("/api/authors") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(CreateAuthorSchema(JET, BRAINS))
            }

            // THEN I get an HTTP 409 status code
            response.status shouldBe HttpStatusCode.Conflict

            // AND I get the message in the body
            response.bodyAsText() shouldBe "DUPLICATE_ENTITY_MESSAGE"
        }
    }

    @ParameterizedTest(name = "{index} -> with exception of type ''{0}''")
    @MethodSource("generateExceptions")
    fun `POST on authors returns HTTP 500 Internal Server Error when the use case throws any other exception`(exception: Exception) {
        val authorUseCases = mockk<AuthorUseCases>()
        coEvery {
            authorUseCases.createAuthor(any<String>(), any<String>())
        } throws (exception)

        testApplication {
            val client = configureApplicationAndCreateClient(authorUseCases)
            // WHEN I post on /api/authors with an author payload
            val response = client.post("/api/authors") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(CreateAuthorSchema(JET, BRAINS))
            }

            // THEN I get an HTTP 500 status code
            response.status shouldBe HttpStatusCode.InternalServerError

            // AND I get the message in the body
            response.bodyAsText() shouldBe "EXCEPTION_MESSAGE"
        }
    }

    companion object {
        @JvmStatic
        fun generateExceptions(): Stream<Arguments> {
            return Stream.of(
                arguments(named("Exception", Exception("EXCEPTION_MESSAGE"))),
                arguments(named("DataWriteException", DataWriteException("EXCEPTION_MESSAGE"))),
                arguments(named("MongoClientException", MongoClientException("EXCEPTION_MESSAGE"))),
            )
        }
    }
}
