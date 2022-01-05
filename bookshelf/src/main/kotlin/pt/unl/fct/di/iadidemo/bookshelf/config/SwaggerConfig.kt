package pt.unl.fct.di.iadidemo.bookshelf.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI? {
        return OpenAPI().info(
            Info().apply {
                title("Book Management -- IADI 2021/22")
                version("1.0")
                description("This is a book management API")
                termsOfService("http://swagger.io/terms/")
                license(
                    License().name("Apache 2.0")
                        .url("http://springdoc.org")
                )
            }
        )
    }
}