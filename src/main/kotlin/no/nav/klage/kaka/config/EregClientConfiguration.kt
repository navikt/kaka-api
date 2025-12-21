package no.nav.klage.kaka.config

import no.nav.klage.kaka.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class EregClientConfiguration(
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${EREG_SERVICES_KAKA_APIKEY}")
    private lateinit var apiKey: String

    @Value("\${EREG_URL}")
    private lateinit var eregServiceURL: String

    @Bean
    fun eregWebClient(): WebClient {
        return webClientBuilder
            .defaultHeader("x-nav-apiKey", apiKey)
            .baseUrl(eregServiceURL)
            .build()
    }
}