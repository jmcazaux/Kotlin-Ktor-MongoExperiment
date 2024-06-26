package com.ironbird.application.data

import com.ironbird.domain.entity.Author
import java.util.UUID

interface AuthorRepository {
    suspend fun saveAuthor(author: Author): Author

    suspend fun findAuthorById(id: UUID): Author

    suspend fun findAuthorByName(firstName: String, lastName: String): Author
}
