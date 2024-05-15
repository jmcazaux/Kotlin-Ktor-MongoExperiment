package com.ironbird.application.data

import com.ironbird.domain.entity.Author
import java.util.*

interface AuthorRepository {
    suspend fun saveAuthor(author: Author): String

    suspend fun findAuthorById(id: UUID): Author

    suspend fun findAuthorByName(firstName: String, lastName: String): Author
}