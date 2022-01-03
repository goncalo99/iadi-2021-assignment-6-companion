package pt.unl.fct.di.iadidemo.bookshelf.presentation.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("books")
interface PublicBooksAPI<T> {
    @GetMapping
    fun getAll():List<T>;


}