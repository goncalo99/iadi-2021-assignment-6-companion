package pt.unl.fct.di.iadidemo.bookshelf.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.web.filter.GenericFilterBean
import pt.unl.fct.di.iadidemo.bookshelf.application.services.UserService
import pt.unl.fct.di.iadidemo.bookshelf.domain.UserDAO
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object JWTSecret {
    private const val passphrase = "este é um grande segredo que tem que ser mantido escondido"
    val KEY: String = Base64.getEncoder().encodeToString(passphrase.toByteArray())
    const val SUBJECT = "JSON Web Token for CIAI 2019/20"
    const val VALIDITY = 1000 * 60 * 10 // 10 minutes in milliseconds
}

private fun addResponseToken(authentication: Authentication, response: HttpServletResponse) {

    val claims = HashMap<String, Any?>()
    claims["username"] = authentication.name

    val token = Jwts
        .builder()
        .setClaims(claims)
        .setSubject(JWTSecret.SUBJECT)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + JWTSecret.VALIDITY))
        .signWith(SignatureAlgorithm.HS256, JWTSecret.KEY)
        .compact()

    response.addHeader("Authorization", "Bearer $token")
}

private fun addCustomResponseToken(authentication: Authentication, response: HttpServletResponse, roles: List<String>) {

    val claims = HashMap<String, Any?>()
    claims["username"] = authentication.name
    claims["roles"] = roles

    val token = Jwts
        .builder()
        .setClaims(claims)
        .setSubject(JWTSecret.SUBJECT)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + JWTSecret.VALIDITY))
        .signWith(SignatureAlgorithm.HS256, JWTSecret.KEY)
        .compact()

    response.addHeader("Authorization", "Bearer $token")
}


class UserPasswordAuthenticationFilterToJWT(
    defaultFilterProcessesUrl: String?,
    private val anAuthenticationManager: AuthenticationManager,
    private val users: UserService
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    override fun attemptAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): Authentication? {
        //getting user from request body
        val user = ObjectMapper().readValue(request!!.inputStream, UserDAO::class.java)

        // perform the "normal" authentication
        val auth =
            anAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(user.username, user.password))

        return if (auth.isAuthenticated) {
            // Proceed with an authenticated user
            SecurityContextHolder.getContext().authentication = auth
            auth
        } else
            null
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain?,
        auth: Authentication
    ) {
        // When returning from the Filter loop, add the token to the response
        val user = users.findUser(auth.name)
        addCustomResponseToken(auth, response, user.get().roles.map { it -> it.tag })
    }
}

class UserAuthToken(private var user: UserDetails) : Authentication {

    override fun getAuthorities() = user.authorities

    override fun setAuthenticated(isAuthenticated: Boolean) {}

    override fun getName() = user.username

    override fun getCredentials() = user.password

    override fun getPrincipal() = this

    override fun isAuthenticated() = true

    override fun getDetails() = user.username
}

class JWTAuthenticationFilter(private val users: CustomUserDetailsService) : GenericFilterBean() {

    // To try it out, go to https://jwt.io to generate custom tokens, in this case we only need a name...

    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        chain: FilterChain?
    ) {

        val authHeader = (request as HttpServletRequest).getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Skip 7 characters for "Bearer "
            val claims = Jwts.parser().setSigningKey(JWTSecret.KEY).parseClaimsJws(token).body

            // should check for token validity here (e.g. expiration date, session in db, etc.)
            val exp = (claims["exp"] as Int).toLong()
            if (exp < System.currentTimeMillis() / 1000) // in seconds

                (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED) // RFC 6750 3.1

            else {
                val user = users.loadUserByUsername(claims["username"] as String)
                val authentication = UserAuthToken(user)
                // Can go to the database to get the actual user information (e.g. authorities)

                SecurityContextHolder.getContext().authentication = authentication

                // Renew token with extended time here. (before doFilter)
                addResponseToken(authentication, response as HttpServletResponse)

                chain!!.doFilter(request, response)
            }
        } else {
            chain!!.doFilter(request, response)
        }
    }
}

/**
 * Instructions:
 *
 * http POST :8080/login username=user password=password
 *
 * Observe in the response:
 *
 * HTTP/1.1 200
 * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKU09OIFdlYiBUb2tlbiBmb3IgQ0lBSSAyMDE5LzIwIiwiZXhwIjoxNTcxNzc2MTM4LCJpYXQiOjE1NzE3NDAxMzgsInVzZXJuYW1lIjoidXNlciJ9.Mz18cn5xw-7rBXw8KwlWxUDSsfNCqlliiwoIpvYPDzk
 *
 * http :8080/pets Authorization:"Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKU09OIFdlYiBUb2tlbiBmb3IgQ0lBSSAyMDE5LzIwIiwiZXhwIjoxNTcxNzc2MTM4LCJpYXQiOjE1NzE3NDAxMzgsInVzZXJuYW1lIjoidXNlciJ9.Mz18cn5xw-7rBXw8KwlWxUDSsfNCqlliiwoIpvYPDzk"
 *
 */

class UserPasswordSignUpFilterToJWT(
    defaultFilterProcessesUrl: String?,
    private val users: UserService
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    override fun attemptAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): Authentication? {
        //getting user from request body
        val user = ObjectMapper().readValue(request!!.inputStream, UserDAO::class.java)

        return users
            .addUser(user)
            .orElse(null)
            .let {
                val auth = UserAuthToken(
                    CustomUserDetails(
                        it.username,
                        it.password,
                        it.roles.map { r -> SimpleGrantedAuthority(r.tag) }.toMutableList()
                    )
                )
                SecurityContextHolder.getContext().authentication = auth
                auth
            }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain?,
        auth: Authentication
    ) {

        addResponseToken(auth, response)
    }
}
