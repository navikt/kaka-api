package no.nav.klage.kaka.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun getSecureLogger(): Logger = LoggerFactory.getLogger("secure")

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