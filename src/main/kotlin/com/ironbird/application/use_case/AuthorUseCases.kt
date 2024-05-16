package com.ironbird.application.use_case

import com.ironbird.application.data.AuthorRepository
import com.ironbird.domain.entity.Author

class AuthorUseCases(private val authorRepository: AuthorRepository) {
    suspend fun createAuthor(firstName: String, lastName: String): Author? {
        throw NotImplementedError("Not implemented")
//        val author = Author(firstName = firstName, lastName = lastName)
//        authorRepository.saveAuthor(author)
//        return author.id.toString()
    }

    suspend fun getAuthor(id: String): Author? {
        throw NotImplementedError("Not implemented")
    }

    suspend fun updateAuthor(id: String, biography: String): Author? {
        throw NotImplementedError("Not implemented")
    }

    suspend fun deleteAuthor(id: String): Boolean {
        throw NotImplementedError("Not implemented")
    }
}