package no.nav.klage.kaka.exceptions

open class ValidationException(msg: String): RuntimeException(msg)

class KvalitetsvurderingNotFoundException(msg: String): RuntimeException(msg)

class MissingTilgangException(msg: String): RuntimeException(msg)

class KvalitetsvurderingFinalizedException(msg: String): RuntimeException(msg)

class SaksdataFinalizedException(msg: String): RuntimeException(msg)