package pt.unl.fct.di.iadidemo.bookshelf.application.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pt.unl.fct.di.iadidemo.bookshelf.config.UserAuthToken

@Component("securityService")
class SecurityService {

    @Autowired
    var books: BookService? = null

    fun isOwnerOfBook(principal: UserAuthToken, bookID: Long): Boolean {
        val book = books?.getOne(bookID)?.orElse(null);
        if (book != null)
            return book.owner.username == principal.name
        return false
    }
}