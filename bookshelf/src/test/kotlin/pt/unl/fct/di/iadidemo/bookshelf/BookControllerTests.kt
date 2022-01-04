package pt.unl.fct.di.iadidemo.bookshelf

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pt.unl.fct.di.iadidemo.bookshelf.application.services.BookService
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.RoleDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.UserDAO


@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTests {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var books: BookService

    companion object {
        val r1 = RoleDAO("ADMIN")
        val r2 = RoleDAO("REVIEWER")
        val r3 = RoleDAO("USER")
        val u1 =
            UserDAO("user1", BCryptPasswordEncoder().encode("password1"), listOf(r3, r2), "User 1", mutableListOf())
        val u2 = UserDAO("user2", BCryptPasswordEncoder().encode("password2"), listOf(r3), "User 2", mutableListOf())
        val b1 = BookDAO(1, "LOR", mutableListOf(), emptyList(), u1)
        val b2 = BookDAO(2, "Dune", mutableListOf(), emptyList(), u2)

        val l = listOf<BookDAO>(b1, b2)

        val mapper = ObjectMapper()

        val s1 = mapper.writeValueAsString(l)
    }

    @Test
    @WithMockUser(username = "user1", password = "password1", roles = ["USER"])
    fun `Test GET books`() {
        Mockito.`when`(books.getAll()).thenReturn(l)

        val s =
            mvc.perform(get("/user/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(s1))
                .andReturn()
    }

    @Test
    @WithMockUser(username = "admin1", password = "password1", roles = ["ADMIN"])
    fun `Admin Test GET books`() {
        Mockito.`when`(books.getAll()).thenReturn(l)

        val s =
            mvc.perform(get("/admin/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(s1))
                .andReturn()
    }
}