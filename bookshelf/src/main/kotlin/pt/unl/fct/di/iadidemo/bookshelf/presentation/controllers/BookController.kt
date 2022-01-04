package pt.unl.fct.di.iadidemo.bookshelf.presentation.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pt.unl.fct.di.iadidemo.bookshelf.application.services.AuthorService
import pt.unl.fct.di.iadidemo.bookshelf.application.services.BookService
import pt.unl.fct.di.iadidemo.bookshelf.application.services.UserService
import pt.unl.fct.di.iadidemo.bookshelf.config.CanAddBook
import pt.unl.fct.di.iadidemo.bookshelf.config.CanDeleteBook
import pt.unl.fct.di.iadidemo.bookshelf.config.CanSeeBook
import pt.unl.fct.di.iadidemo.bookshelf.config.CanUpdateBook
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.ImageDAO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.BooksAPI
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.AuthorsBookDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.BookDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.BookListDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.ImageDTO

/**
 * This is a sample class implementing the presentation logic layer for REST services,
 * the controller layer.
 *
 * Each controller implements a set of endpoints declared in a API interface. It performs
 * data format transformation and prepares answers to the REST clients.
 *
 * This controller implements two sample endpoints that use and orchestrate methods
 * from one or more components from the service layer. Notice the use of DTO classes
 * to define the types of the enpoint parameters and results. Data transformations are
 * necessary in all cases.
 */

@RestController
class BookController(val books: BookService, val authors: AuthorService, val users: UserService) : BooksAPI {

    @CanAddBook
    override fun addOne(elem: BookDTO): Long {
        val authors = authors.findByIds(elem.authors) // May return 400 (invalid request) if they do not exist
        val owner = users.findUser(elem.owner)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found ${elem.owner}") }
        return books.addOne(
            BookDAO(
                0,
                elem.title,
                authors.toMutableList(),
                elem.images.map { ImageDAO(0, it) },
                owner
            )
        );
    }

    @CanSeeBook
    override fun getOne(id: Long): BookListDTO =
        books
            .getOne(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found ${id}") }
            .let {
                BookListDTO(
                    it.id,
                    it.title,
                    it.authors.map { AuthorsBookDTO(it.name) },
                    it.images.map { ImageDTO(it.url) },
                    it.owner.username
                )
            }

    @CanUpdateBook
    override fun updateOne(id: Long, elem: BookDTO) {
        val authors = authors.findByIds(elem.authors) // May return 400 (invalid request) if they do not exist
        val owner = users.findUser(elem.owner)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found ${elem.owner}") }
        books.updateOne(
            id,
            BookDAO(0, elem.title, authors.toMutableList(), elem.images.map { ImageDAO(0, it) }, owner)
        )
    }

    @CanDeleteBook
    override fun deleteOne(id: Long) {
        println("hello")
        books.getOne(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found ${id}") }
        books.deleteOne(id)
    }
}