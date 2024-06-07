package com.ironbird.application.usecase

import com.ironbird.application.data.AuthorRepository
import com.ironbird.commons.exceptions.DuplicateEntityException
import com.ironbird.domain.entity.Author
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class AuthorUseCasesImplTest {

    @Test
    fun `createAuthor should save the author to the repository and return the saved entity`() {
        // GIVEN An AuthorUseCase with a repository
        val authorRepository = mockk<AuthorRepository>()

        // AND the repository will create an author
        coEvery {
            authorRepository.saveAuthor(any())
        } answers { firstArg() }

        val useCase = AuthorUseCasesImpl(authorRepository)

        // WHEN I add a user
        val createdAuthor: Author
        runBlocking {
            createdAuthor = useCase.createAuthor("John", "Doe")
        }

        // THEN The `save` method of the repository should be called
        coVerify(exactly = 1) {
            authorRepository
                .saveAuthor(any())
        }
        // AND the created author should match the arguments
        createdAuthor.firstName shouldBe "John"
        createdAuthor.lastName shouldBe "Doe"
        createdAuthor.id shouldNotBe null
    }

    @Test
    fun `createAuthor should raise a DuplicateEntityException when the repository throws`() {
        // GIVEN An AuthorUseCase with a repository
        val authorRepository = mockk<AuthorRepository>()

        // AND the repository will throw a DuplicateEntityException
        coEvery {
            authorRepository.saveAuthor(any())
        } throws DuplicateEntityException("SOME_MESSAGE")

        val useCase = AuthorUseCasesImpl(authorRepository)

        // WHEN I add a user
        // THEN A DuplicateEntityException should be thrown
        val exception = shouldThrow<DuplicateEntityException> {
            runBlocking {
                useCase.createAuthor("John", "Doe")
            }
        }

        // AND the exception message should not be altered
        exception.message shouldBe "SOME_MESSAGE"
    }
}
