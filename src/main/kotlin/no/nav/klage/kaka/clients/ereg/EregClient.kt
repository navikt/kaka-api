package no.nav.klage.kaka.clients.ereg


import brave.Tracer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.klage.kaka.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class EregClient(
    private val eregWebClient: WebClient,
    private val tracer: Tracer
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    private fun hentOrganisasjon(orgnummer: String): Organisasjon? {
        return kotlin.runCatching {
            eregWebClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/v1/organisasjon/{orgnummer}")
                        .queryParam("inkluderHierarki", false)
                        .build(orgnummer)
                }
                .accept(MediaType.APPLICATION_JSON)
                .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
                .header("Nav-Consumer-Id", applicationName)
                .retrieve()
                .bodyToMono<Organisasjon>()
                .block()
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is WebClientResponseException.NotFound -> {
                        logger.error("$orgnummer not found in ereg")
                        null
                    }
                    else -> {
                        logger.error("Error from ereg: $error")
                        null
                    }
                }
            }
        )
    }

    fun organisasjonExists(orgnummer: String): Boolean {
        return hentOrganisasjon(orgnummer) != null
    }
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class Organisasjon(
    val navn: Navn,
    val organisasjonsnummer: String,
    val type: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Navn(
    val navnelinje1: String?,
    val navnelinje2: String?,
    val navnelinje3: String?,
    val navnelinje4: String?,
    val navnelinje5: String?,
    val redigertnavn: String?
) {
    fun sammensattNavn(): String =
        listOfNotNull(navnelinje1, navnelinje2, navnelinje3, navnelinje4, navnelinje5).joinToString(separator = " ")
}