package com.ironbird.domain.entity

import java.util.*

data class BookAuthor(
    var id: UUID,
    var firstName: String,
    var lastName: String,
) {
    constructor(author: Author) : this(author.id, author.firstName, author.lastName)
}

data class Book(val id: UUID, var title: String, var author: BookAuthor, var description: String?)
