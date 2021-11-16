package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SaksdataView (
    val id: UUID,
    var sakenGjelder: String? = null,
    var sakstype: String? = null,
    var tema: String? = null,
    var ytelse: String? = null,
    var mottattVedtaksinstans: LocalDate? = null,
    var vedtaksinstansEnhet: String? = null,
    var mottattKlageinstans: LocalDate? = null,
    var utfall: String? = null,
    var hjemler: List<String> = emptyList(),
    var utfoerendeSaksbehandler: String,
    var kvalitetsvurderingId: UUID,
    var avsluttetAvSaksbehandler: LocalDateTime? = null,
    val created: LocalDateTime,
    var modified: LocalDateTime
)

fun Saksdata.toSaksdataView(): SaksdataView {
    return SaksdataView(
        id = id,
        sakenGjelder = sakenGjelder,
        sakstype = sakstype?.id,
        tema = tema?.id,
        ytelse = ytelse?.id,
        mottattVedtaksinstans = mottattVedtaksinstans,
        vedtaksinstansEnhet = vedtaksinstansEnhet,
        mottattKlageinstans = mottattKlageinstans,
        utfall = utfall?.id,
        hjemler = hjemler?.map { it.id } ?: emptyList(),
        utfoerendeSaksbehandler = utfoerendeSaksbehandler,
        kvalitetsvurderingId = kvalitetsvurdering.id,
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        created = created,
        modified = modified
    )
}