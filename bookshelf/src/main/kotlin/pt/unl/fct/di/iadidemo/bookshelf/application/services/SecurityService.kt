package pt.unl.fct.di.iadidemo.bookshelf.application.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component


@Component("securityService")
class SecurityService {

    @Autowired
    var books: BookService? = null

    fun isOwnerOfBook(principal: UserDetails, bookID: Long): Boolean {
        val book = books?.getOne(bookID)?.orElse(null);
        if (book != null)
            return book.owner.username == principal.username
        return false
    }
}