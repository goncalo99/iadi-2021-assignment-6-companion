package pt.unl.fct.di.iadidemo.bookshelf.domain

import javax.persistence.*


@Entity
data class BookDAO(
    @Id @GeneratedValue
    val id: Long,

    var title: String,

    @ManyToMany
    var authors: MutableList<AuthorDAO>,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var images: List<ImageDAO>,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var owner: UserDAO
)


@Entity
data class AuthorDAO(
    @Id @GeneratedValue
    val id: Long,

    val name: String,

    )

@Entity
data class ImageDAO(
    @Id @GeneratedValue
    val id: Long,

    val url: String,

    )

@Entity
data class UserDAO(
    @Id
    val username: String,

    var password: String,

    @ManyToMany(fetch = FetchType.EAGER)
    val roles: List<RoleDAO>,

    val name: String,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var books: List<BookDAO>,

    )

@Entity
data class RoleDAO(
    @Id
    val tag: String
)