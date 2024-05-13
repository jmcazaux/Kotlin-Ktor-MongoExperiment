package com.ironbird.domain.entity

import java.util.*

/**
 * Represents an author of a book.
 * @property id The unique identifier of the author. If none is provided on initialization a random one will be generated.
 * @property firstName The first name of the author.
 * @property lastName The last name of the author.
 */
data class Author(
    val id: UUID = UUID.randomUUID(),
    var firstName: String,
    var lastName: String,
) {
    var biography: String? = null
}