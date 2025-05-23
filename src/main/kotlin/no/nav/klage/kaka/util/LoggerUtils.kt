package no.nav.klage.kaka.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import java.util.*

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun getTeamLogger(): Logger = LoggerFactory.getLogger("team-logs")

fun logSaksdataMethodDetails(methodName: String, innloggetIdent: String, saksdataId: UUID?, logger: Logger) {
    logger.debug(
        "{} is requested by ident {} for saksdataId {}",
        methodName,
        innloggetIdent,
        saksdataId
    )
}

fun logKvalitetsvurderingMethodDetails(methodName: String, innloggetIdent: String, kvalitetsvurderingId: UUID, logger: Logger) {
    logger.debug(
        "{} is requested by ident {} for kvalitetsvurderingId {}",
        methodName,
        innloggetIdent,
        kvalitetsvurderingId
    )
}

fun logErrorResponse(response: ClientResponse, functionName: String, classLogger: Logger): Mono<RuntimeException> {
    return response.bodyToMono(String::class.java).map {
        val errorString = "Got ${response.statusCode()} when requesting $functionName"
        classLogger.error("$errorString. See team-logs for more details.")
        getTeamLogger().error("$errorString - response body: '$it'")
        RuntimeException(errorString)
    }
}