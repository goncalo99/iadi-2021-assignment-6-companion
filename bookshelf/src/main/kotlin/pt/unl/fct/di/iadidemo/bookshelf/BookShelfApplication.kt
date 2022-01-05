package pt.unl.fct.di.iadidemo.bookshelf

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.unl.fct.di.iadidemo.bookshelf.domain.*
import javax.transaction.Transactional

@SpringBootApplication
class SecurityApplication(
    val books: BookRepository,
    val users: UserRepository,
    val roles: RoleRepository,
    val authors: AuthorRepository
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {

        val r1 = RoleDAO("ADMIN")
        val r2 = RoleDAO("REVIEWER")
        val r3 = RoleDAO("USER")

        val u1 = UserDAO("user1", BCryptPasswordEncoder().encode("password1"), listOf(r3, r2), "User 1", emptyList())
        val u2 = UserDAO("user2", BCryptPasswordEncoder().encode("password2"), listOf(r3), "User 2", emptyList())
        val u3 = UserDAO("admin1", BCryptPasswordEncoder().encode("password1"), listOf(r1), "Admin 1", emptyList())
        val u4 = UserDAO("admin2", BCryptPasswordEncoder().encode("password2"), listOf(r1), "Admin 2", emptyList())

        val a1 = AuthorDAO(0, "Philip K. Dick")
        val a2 = AuthorDAO(0, "Author1")
        val a3 = AuthorDAO(0, "Author2")
        val a4 = AuthorDAO(0, "Author3")

        authors.save(a1)
        authors.save(a2)
        authors.save(a3)
        authors.save(a4)


        val b1 = BookDAO(
            0,
            "Ubik",
            mutableListOf(a1),
            listOf(ImageDAO(0, "https://covers.openlibrary.org/b/id/9251896-L.jpg")),
            u1
        )
        val b2 = BookDAO(
            0,
            "Do Androids Dream of Electric Sheep?",
            mutableListOf(a1,a2,a3,a4),
            listOf(ImageDAO(0, "https://covers.openlibrary.org/b/id/11153217-L.jpg")),
            u2
        )
        val b3 = BookDAO(
            0,
            "The Man in the High Castle",
            mutableListOf(a1),
            listOf(ImageDAO(0, "https://covers.openlibrary.org/b/id/10045188-L.jpg")),
            u3
        )

        books.saveAll(listOf(b1, b2, b3))
        users.save(u4)

    }

}

fun main(args: Array<String>) {
    runApplication<SecurityApplication>(*args)
}