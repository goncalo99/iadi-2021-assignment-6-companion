package pt.unl.fct.di.iadidemo.bookshelf.presentation.controllers

import org.springframework.web.bind.annotation.RestController
import pt.unl.fct.di.iadidemo.bookshelf.application.services.BookService
import pt.unl.fct.di.iadidemo.bookshelf.config.CanSeeBooks
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.PublicBooksAPI
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.AuthorsBookDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.BookListDTO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.ImageDTO

@RestController
class PublicBookController(val books: BookService) : PublicBooksAPI<BookListDTO> {
    @CanSeeBooks
    override fun getAll(): List<BookListDTO> =
            books.getAll().map {
                BookListDTO(
                        it.id,
                        it.title,
                        it.authors.map { AuthorsBookDTO(it.name) },
                        it.images.map { ImageDTO(it.url) },
                        it.owner
                )
            }
}