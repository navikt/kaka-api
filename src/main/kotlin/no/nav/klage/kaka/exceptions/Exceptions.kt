package no.nav.klage.kaka.exceptions

open class ValidationException(msg: String) : RuntimeException(msg)

class KvalitetsvurderingNotFoundException(msg: String) : RuntimeException(msg)

class SaksdataNotFoundException(msg: String) : RuntimeException(msg)

class MissingTilgangException(msg: String) : RuntimeException(msg)

class KvalitetsvurderingFinalizedException(msg: String) : RuntimeException(msg)

class SaksdataFinalizedException(msg: String) : RuntimeException(msg)

class ValidationErrorWithDetailsException(val title: String, val invalidProperties: List<InvalidProperty>) :
    RuntimeException() {

    data class InvalidProperty(val field: String, val reason: String)
}