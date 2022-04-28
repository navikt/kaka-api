package no.nav.klage.kaka.clients.axsys

import brave.Tracer
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class AxsysClient(
    private val axsysWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    private val tracer: Tracer
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    //@Retryable
    //@Cacheable(TILGANGER_CACHE)
    fun getTilgangerForSaksbehandler(navIdent: String): Tilganger {
        logger.debug("Fetching tilganger for saksbehandler with Nav-Ident {}", navIdent)

        return try {
            val tilganger = axsysWebClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/tilgang/{navIdent}")
                        .queryParam("inkluderAlleEnheter", "true")
                        .build(navIdent)
                }
                .header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithAxsysScope()}")
                .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
                .header("Nav-Consumer-Id", applicationName)

                .retrieve()
                .bodyToMono<Tilganger>()
                .block() ?: throw RuntimeException("Tilganger could not be fetched")
            tilganger
        } catch (notFound: WebClientResponseException.NotFound) {
            logger.warn("Got a 404 fetching tilganger for saksbehandler {}, throwing exception", navIdent, notFound)
            throw RuntimeException("Tilganger could not be fetched")
        }
    }
}



