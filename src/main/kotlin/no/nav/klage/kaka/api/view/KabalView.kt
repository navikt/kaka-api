package no.nav.klage.kaka.api.view

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class KabalView(
    val id: UUID,
    val kvalitetsvurderingId: UUID,
    val kvalitetsvurderingVersion: Int,
)

data class KabalViewIdOnly(
    val id: UUID,
)

data class KabalSaksdataInput(
    val sakenGjelder: String,
    val sakstype: String,
    val ytelseId: String,
    val mottattVedtaksinstans: LocalDate?,
    val vedtaksinstansEnhet: String,
    val mottattKlageinstans: LocalDate,
    val utfall: String,
    val registreringshjemler: List<String>?,
    val utfoerendeSaksbehandler: String,
    val kvalitetsvurderingId: UUID,
    val avsluttetAvSaksbehandler: LocalDateTime,
    val tilknyttetEnhet: String,
)

data class ValidationErrors(
    val validationErrors: List<InvalidProperty>
) {
    data class InvalidProperty(val field: String, val reason: String)
}