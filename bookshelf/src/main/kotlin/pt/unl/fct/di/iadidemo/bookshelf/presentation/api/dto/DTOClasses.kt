package pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto

import pt.unl.fct.di.iadidemo.bookshelf.domain.UserDAO


data class BookDTO(val title: String, val authors: List<Long>, val images: List<String>, val owner: String)

data class ImageDTO(val url: String)

data class BookListDTO(
    val id: Long,
    val title: String,
    val authors: List<AuthorsBookDTO>,
    val images: List<ImageDTO>,
    val owner: String
)

data class AuthorsBookDTO(val name: String)

data class AuthorsBookListDTO(val name: String,val id:Long)



