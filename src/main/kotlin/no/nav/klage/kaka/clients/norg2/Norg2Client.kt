package no.nav.klage.kaka.clients.norg2

import brave.Tracer
import no.nav.klage.kaka.exceptions.EnhetNotFoundForSaksbehandlerException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Component
class Norg2Client(
    private val norg2WebClient: WebClient,
    private val tracer: Tracer
) {

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
                .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
                .header("Nav-Consumer-Id", applicationName)
                .retrieve()
                .bodyToMono<Norg2Enhet>()
                .block()
        }.fold(
            onSuccess = { it },
            onFailure = {
                throw EnhetNotFoundForSaksbehandlerException("Enhet ikke funnet med enhetNr $enhetsnummer")
            }
        )
    }

}