package com.ironbird.application.usecase

import com.ironbird.application.data.AuthorRepository
import com.ironbird.domain.entity.Author

interface AuthorUseCases {
    suspend fun createAuthor(firstName: String, lastName: String): Author
    suspend fun getAuthor(id: String): Author?
    suspend fun updateAuthor(id: String, biography: String): Author?
    suspend fun deleteAuthor(id: String): Boolean
}

class AuthorUseCasesImpl(private val authorRepository: AuthorRepository) : AuthorUseCases {
    override suspend fun createAuthor(firstName: String, lastName: String): Author {
        val author = Author(firstName = firstName, lastName = lastName)
        authorRepository.saveAuthor(author)
        return author
    }


    override suspend fun getAuthor(id: String): Author? {
        TODO("Not implemented yet!")
    }

    override suspend fun updateAuthor(id: String, biography: String): Author? {
        TODO("Not implemented yet!")
    }

    override suspend fun deleteAuthor(id: String): Boolean {
        TODO("Not implemented yet!")
    }
}
