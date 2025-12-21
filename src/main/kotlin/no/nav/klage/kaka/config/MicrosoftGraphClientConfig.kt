package no.nav.klage.kaka.config

import no.nav.klage.kaka.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class MicrosoftGraphClientConfig(
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${MICROSOFT_GRAPH_URL}")
    private lateinit var microsoftGraphServiceURL: String

    @Bean
    fun microsoftGraphWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(microsoftGraphServiceURL)
            .build()
    }
}