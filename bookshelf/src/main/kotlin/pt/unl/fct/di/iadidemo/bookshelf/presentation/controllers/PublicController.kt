package pt.unl.fct.di.iadidemo.bookshelf.presentation.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.unl.fct.di.iadidemo.bookshelf.application.services.AuthorService
import pt.unl.fct.di.iadidemo.bookshelf.application.services.BookService
import pt.unl.fct.di.iadidemo.bookshelf.config.CanSeeBooks
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.AuthorsBookDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.AuthorsBookListDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.BookListDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.ImageDTO

@RestController
class PublicController(val books: BookService, val authors: AuthorService) {
    @CanSeeBooks
    @GetMapping("/books")
    fun getBooks(): List<BookListDTO> =
            books.getAll().map {
                BookListDTO(
                        it.id,
                        it.title,
                        it.authors.map { AuthorsBookDTO(it.name) },
                        it.images.map { ImageDTO(it.url) },
                        it.owner.username
                )
            }

    @CanSeeBooks
    @GetMapping("/authors")
    fun getAuthors(): List<AuthorsBookListDTO> =
            authors.getAll().map {
                AuthorsBookListDTO(
                        it.name,
                        it.id
                )   
            }
}