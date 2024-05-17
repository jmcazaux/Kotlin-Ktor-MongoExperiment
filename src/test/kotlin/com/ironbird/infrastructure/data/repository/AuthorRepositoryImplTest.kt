package com.ironbird.infrastructure.data.repository

import com.ironbird.commons.exceptions.DuplicateEntityException
import com.ironbird.domain.entity.Author
import com.ironbird.infrastructure.data.AuthorSchemaFields
import com.ironbird.infrastructure.data.DATABASE_NAME
import com.ironbird.infrastructure.data.createMongoClientAndDatabase
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@Testcontainers
class AuthorRepositoryImplTest {

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
    fun `saveAuthor should save and return the author when not exists`() {
        // GIVEN an AuthorRepository initialised with a Mongo Database
        val repository = AuthorRepositoryImpl(database)

        // WHEN I save an author
        val author = Author(firstName = "John", lastName = "Doe")
        val returnedAuthor: Author?
        runBlocking {
            returnedAuthor = repository.saveAuthor(author)
        }

        // THEN the returned Author should be "author"
        returnedAuthor shouldBe author

        // AND author should be saved into the database's "authors" collection
        val authorCollection = database.getCollection<Map<String, Any>>("authors")
        val savedAuthor =
            runBlocking {
                authorCollection.find(Filters.eq(AuthorSchemaFields.ID.value, author.id)).first()
            }

        savedAuthor["firstName"] shouldBe "John"
        savedAuthor["lastName"] shouldBe "Doe"

    }

    @Test
    fun `saveAuthor should throw a duplicate entity exception when firstname and lastname exist`() {
        // GIVEN an AuthorRepository initialised with a Mongo Database
        val repository = AuthorRepositoryImpl(database)

        // AND I added a "John", "Doe" author
        runBlocking {
            repository.saveAuthor(Author(firstName = "John", lastName = "Doe"))
        }

        // WHEN I add another author also named John Doe
        // THEN I should get a DuplicateEntityException
        val exception = shouldThrow<DuplicateEntityException> {
            runBlocking {
                repository.saveAuthor(Author(firstName = "John", lastName = "Doe"))
            }
        }
        // AND the message should be relevant
        exception.message should startWith("Author \"John Doe\" already exists")
    }

    @Test
    fun `saveAuthor should throw a duplicate entity exception when ID exists`() {
        // GIVEN an AuthorRepository initialised with a Mongo Database
        val repository = AuthorRepositoryImpl(database)

        // AND I added a "John", "Doe" author with ID id1
        val id1 = UUID.randomUUID()
        runBlocking {
            repository.saveAuthor(Author(id = id1, firstName = "John", lastName = "Doe"))
        }

        // WHEN I add another author with id id1 but named Joe Blogs
        // THEN I should get a DuplicateEntityException
        val exception = shouldThrow<DuplicateEntityException> {
            runBlocking {
                repository.saveAuthor(Author(id = id1, firstName = "Joe", lastName = "Blogs"))
            }
        }
        // AND the message should be relevant
        exception.message should startWith("Author with ID \"${id1}\" already exists")
    }

    @Test
    fun `findAuthorById should return the author when exists`() {
        throw NotImplementedError("Not implemented")
    }

    @Test
    fun `findAuthorById should return null when not exists`() {
        throw NotImplementedError("Not implemented")
    }

    @Test
    fun `findAuthorByName  should return the author when exists`() {
        throw NotImplementedError("Not implemented")
    }
}
