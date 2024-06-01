package com.ironbird.e2etest

import com.ironbird.helpers.checkSavedAuthor
import com.ironbird.infrastructure.api.AuthorSchema
import com.ironbird.infrastructure.api.CreateAuthorSchema
import com.ironbird.infrastructure.data.DATABASE_NAME
import com.ironbird.infrastructure.data.createMongoClientAndDatabase
import com.ironbird.module
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

private fun ApplicationTestBuilder.configureApplicationAndCreateClient(mongoServer: MongoDBContainer): HttpClient {
    val mongoPort = mongoServer.getMappedPort(27017)
    environment {
        config = config.mergeWith(MapApplicationConfig("db.mongo.port" to "$mongoPort"))
    }

    application {
        module()
    }


    // AND a client
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

@Testcontainers
class AuthorsApiTest {


    companion object {
        @Container
        private val MONGO_SERVER = MongoDBContainer("mongo:latest")
            .withExposedPorts(27017)
    }

    private lateinit var mongoClient: MongoClient
    private lateinit var database: MongoDatabase


    @BeforeEach
    fun connectToDb() {
        val mongoPort = MONGO_SERVER.getMappedPort(27017)
        val uri = "mongodb://127.0.0.1:$mongoPort/?maxPoolSize=20&w=majority"

        with(createMongoClientAndDatabase(uri, DATABASE_NAME)) {
            mongoClient = first
            database = second
        }
    }


    @AfterEach
    fun closeDb() {
        runBlocking {
            database.drop()
        }
        mongoClient.close()
    }


    @Test
    fun `POST on authors should return the created author`() {

        // GIVEN An application configured with a database
        testApplication {
            val client = configureApplicationAndCreateClient(MONGO_SERVER)

            // WHEN the client POSTs on /api/authors with the correct payload
            val response = client.post("/api/authors") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(CreateAuthorSchema("Jet", "Brains"))
            }

            // THEN I get an HTTP 200
            response.status shouldBe HttpStatusCode.OK

            // AND the author is returned
            val returnedAuthor = Json.decodeFromString<AuthorSchema>(response.bodyAsText())
            returnedAuthor.firstName shouldBe "Jet"
            returnedAuthor.lastName shouldBe "Brains"
            returnedAuthor.id shouldNotBe null

            // AND the author has been saved into the database
            checkSavedAuthor(
                database,
                savedAuthorId = UUID.fromString(returnedAuthor.id),
                expectedFirstname = "Jet",
                expectedLastname = "Brains"
            )
        }
    }


    @Test
    fun `POST on authors should return HTTP 409 Conflict when creating an existing author (firstname, lastname)`() {

        // GIVEN An application configured with a database
        testApplication {
            val client = configureApplicationAndCreateClient(MONGO_SERVER)

            // AND a "John Doe" author is created
            client.post("/api/authors") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(CreateAuthorSchema("John", "Doe"))
            }

            // WHEN I try to create a John Doe again
            val response = client.post("/api/authors") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(CreateAuthorSchema("John", "Doe"))
            }

            // THEN I get an HTTP 409
            response.status shouldBe HttpStatusCode.Conflict

            // AND the error message is meaningful (Author "John Doe" already exists... Author not saved.)
            response.bodyAsText() shouldMatch "Author.*John Doe.*exists.*not saved.*"
        }
    }
}

