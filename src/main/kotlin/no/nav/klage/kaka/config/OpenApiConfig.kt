package no.nav.klage.kaka.config

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("standard")
            .pathsToMatch("/**")
            .build()
    }

}