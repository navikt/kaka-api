package no.nav.klage.kaka.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun getSecureLogger(): Logger = LoggerFactory.getLogger("secure")

fun logVurderingMethodDetails(methodName: String, innloggetIdent: String, vurderingId: UUID?, logger: Logger) {
    logger.debug(
        "{} is requested by ident {} for vurderingId {}",
        methodName,
        innloggetIdent,
        vurderingId
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