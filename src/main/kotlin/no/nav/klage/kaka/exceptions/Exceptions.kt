package no.nav.klage.kaka.exceptions

open class ValidationException(msg: String) : RuntimeException(msg)

class KvalitetsvurderingNotFoundException(msg: String) : RuntimeException(msg)

class SaksdataNotFoundException(msg: String) : RuntimeException(msg)

class MissingTilgangException(msg: String) : RuntimeException(msg)

class KvalitetsvurderingFinalizedException(msg: String) : RuntimeException(msg)

class SaksdataFinalizedException(msg: String) : RuntimeException(msg)

class EnhetNotFoundForSaksbehandlerException(msg: String) : RuntimeException(msg)

data class InvalidProperty(val field: String, val reason: String)

class SectionedValidationErrorWithDetailsException(val title: String, val sections: List<ValidationSection>) :
    RuntimeException()

data class ValidationSection(val section: String, val properties: List<InvalidProperty>)