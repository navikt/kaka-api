package no.nav.klage.kaka.clients.ereg


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.opentelemetry.api.trace.Span
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import no.nav.klage.kaka.util.logErrorResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class EregClient(
    private val eregWebClient: WebClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
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
                .header("Nav-Call-Id", Span.current().spanContext.traceId)
                .header("Nav-Consumer-Id", applicationName)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    logErrorResponse(response, ::hentOrganisasjon.name, secureLogger)
                }
                .bodyToMono<Organisasjon>()
                .block()
        }.fold(
            onSuccess = { it },
            onFailure = { error ->
                when (error) {
                    is WebClientResponseException.NotFound -> {
                        logger.error("$orgnummer not found in ereg", error)
                        null
                    }
                    else -> {
                        logger.error("Error from ereg.", error)
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