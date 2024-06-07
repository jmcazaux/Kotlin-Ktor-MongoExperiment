package com.ironbird.infrastructure.data

import com.ironbird.domain.entity.Author
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

enum class AuthorSchemaFields(val value: String) {
    MONGO_ID("_id"),
    ID("id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    BIOGRAPHY("biography")
}

data class AuthorSchema(
    @BsonId
    val mongoId: ObjectId = ObjectId(),
    val id: UUID,
    var firstName: String,
    var lastName: String,
    var biography: String? = null
) {

    constructor(author: Author) : this(
        id = author.id,
        firstName = author.firstName,
        lastName = author.lastName,
        biography = author.biography
    )

    fun toAuthor(): Author {
        return Author(UUID.fromString(this.id.toString()), this.firstName, this.lastName, this.biography)
    }
}
