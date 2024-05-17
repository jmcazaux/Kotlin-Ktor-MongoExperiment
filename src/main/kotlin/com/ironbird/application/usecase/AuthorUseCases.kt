package com.ironbird.application.usecase

import com.ironbird.application.data.AuthorRepository
import com.ironbird.domain.entity.Author

interface AuthorUseCases {
    fun createAuthor(firstName: String, lastName: String): Author?
    fun getAuthor(id: String): Author?
    fun updateAuthor(id: String, biography: String): Author?
    fun deleteAuthor(id: String): Boolean
}

class AuthorUseCasesImpl(private val authorRepository: AuthorRepository) : AuthorUseCases {
    override fun createAuthor(firstName: String, lastName: String): Author? {
        throw NotImplementedError("Not implemented")
//        val author = Author(firstName = firstName, lastName = lastName)
//        authorRepository.saveAuthor(author)
//        return author.id.toString()
    }


    override fun getAuthor(id: String): Author? {
        throw NotImplementedError("Not implemented")
    }

    override fun updateAuthor(id: String, biography: String): Author? {
        throw NotImplementedError("Not implemented")
    }

    override fun deleteAuthor(id: String): Boolean {
        throw NotImplementedError("Not implemented")
    }
}