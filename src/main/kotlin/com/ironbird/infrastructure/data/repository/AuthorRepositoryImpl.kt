package com.ironbird.infrastructure.data.repository

import com.ironbird.application.data.AuthorRepository
import com.ironbird.commons.exceptions.DuplicateEntityException
import com.ironbird.commons.exceptions.WriteDataException
import com.ironbird.domain.entity.Author
import com.ironbird.infrastructure.data.AuthorSchema
import com.ironbird.infrastructure.data.AuthorSchemaFields
import com.mongodb.MongoWriteException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

internal val LOGGER = KtorSimpleLogger(AuthorRepositoryImpl::class.simpleName!!)
private const val AUTHORS_COLLECTION = "authors"

private val DUPLICATE_AUTHOR_NAME_MESSAGE_REGEX = Regex(
    "E11000 duplicate key .* dup key: \\{ firstName: .*, lastName: .* }"
)
private val DUPLICATE_ID_MESSAGE_REGEX = Regex(
    "E11000 duplicate key .* dup key: \\{ id: .* }"
)

class AuthorRepositoryImpl(private val database: MongoDatabase) : AuthorRepository {
    private var collection: MongoCollection<AuthorSchema>

    init {
        runBlocking {
            database.createCollection(AUTHORS_COLLECTION)
            collection = database.getCollection(AUTHORS_COLLECTION)
            collection.createIndex(
                Indexes.ascending(
                    AuthorSchemaFields.FIRST_NAME.value,
                    AuthorSchemaFields.LAST_NAME.value
                ), IndexOptions().unique(true)
            )
            collection.createIndex(Indexes.ascending(AuthorSchemaFields.ID.value), IndexOptions().unique(true))
        }
    }

    override suspend fun saveAuthor(author: Author) = withContext(Dispatchers.IO) {
        try {
            collection.insertOne(AuthorSchema(author)).insertedId
                ?: throw WriteDataException("Author not saved.")
            author
        } catch (e: MongoWriteException) {
            // e.error.message have the following form:
            // "E11000 duplicate key error collection: <db.coll> index: <name> dup key: { k1: v1, k2: v2<, ...> }"
            // The below identifies the actual duplicated  key in order to form a meaningful message
            val mongoMessage = e.error.message
            val message = when {
                DUPLICATE_AUTHOR_NAME_MESSAGE_REGEX.containsMatchIn(mongoMessage) ->
                    "Author \"${author.firstName} ${author.lastName}\" already exists"
                DUPLICATE_ID_MESSAGE_REGEX.containsMatchIn(mongoMessage) ->
                    "Author with ID \"${author.id}\" already exists"
                else -> "Unexpected database write error"
            }

            throw DuplicateEntityException("$message... Author not saved.", e)
        }
    }

    override suspend fun findAuthorById(id: UUID): Author = withContext(Dispatchers.IO) {
        collection.find(Filters.eq(AuthorSchemaFields.ID.value, id.toString())).first().toAuthor()
    }

    override suspend fun findAuthorByName(firstName: String, lastName: String): Author = withContext(Dispatchers.IO) {
        collection.find(
            Filters.and(
                Filters.eq(AuthorSchemaFields.FIRST_NAME.value, firstName),
                Filters.eq(AuthorSchemaFields.LAST_NAME.value, lastName)
            )
        )
            .first()
            .toAuthor()
    }
}
