package com.ironbird.helpers

import com.ironbird.infrastructure.data.AuthorSchemaFields
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Checks that an author with savedAuthorId has been saved in database, and it has expectedFirstname
 * and expectedLastname
 */
fun checkSavedAuthor(
    database: MongoDatabase,
    savedAuthorId: UUID,
    expectedFirstname: String,
    expectedLastname: String
) {
    val authorCollection = database.getCollection<Map<String, Any>>("authors")
    val savedAuthor =
        runBlocking {
            authorCollection.find(Filters.eq(AuthorSchemaFields.ID.value, savedAuthorId)).first()
        }

    savedAuthor["firstName"] shouldBe expectedFirstname
    savedAuthor["lastName"] shouldBe expectedLastname
}
