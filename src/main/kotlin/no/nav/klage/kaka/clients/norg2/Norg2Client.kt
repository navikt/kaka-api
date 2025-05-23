package no.nav.klage.kaka.clients.norg2

import io.opentelemetry.api.trace.Span
import no.nav.klage.kaka.exceptions.EnhetNotFoundForSaksbehandlerException
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logErrorResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Component
class Norg2Client(
    private val norg2WebClient: WebClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    fun getEnhet(enhetsnummer: String): Norg2Enhet? {
        return kotlin.runCatching {
            norg2WebClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/enhet/{enhetsnummer}")
                        .build(enhetsnummer)
                }
                .accept(MediaType.APPLICATION_JSON)
                .header("Nav-Call-Id", Span.current().spanContext.traceId)
                .header("Nav-Consumer-Id", applicationName)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    logErrorResponse(
                        response = response,
                        functionName = ::getEnhet.name,
                        classLogger = logger,
                    )
                }
                .bodyToMono<Norg2Enhet>()
                .block()
        }.fold(
            onSuccess = { it },
            onFailure = {
                logger.error("Error from Norg2.", it)
                throw EnhetNotFoundForSaksbehandlerException("Enhet ikke funnet med enhetNr $enhetsnummer, error: $it")
            }
        )
    }
}