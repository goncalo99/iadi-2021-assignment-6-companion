package pt.unl.fct.di.iadidemo.bookshelf.application.services

import org.springframework.stereotype.Service
import pt.unl.fct.di.iadidemo.bookshelf.domain.AuthorDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.AuthorRepository
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookDAO

@Service
class AuthorService(val authors:AuthorRepository) {

    fun getAll(): List<AuthorDAO> = authors.findAll().toList()

    fun findByIds(ids:List<Long>): List<AuthorDAO> =
        ids.map { authors.findById(it) }.filter { it.isPresent }.map { it.get() }

}