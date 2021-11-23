package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SaksdataView (
    val id: UUID,
    var sakenGjelder: String?,
    var sakstype: String?,
    var sakstypeId: String?,
    var tema: String?,
    var temaId: String?,
    var ytelse: String?,
    var ytelseId: String?,
    var mottattVedtaksinstans: LocalDate?,
    var vedtaksinstansEnhet: String?,
    var mottattKlageinstans: LocalDate?,
    var utfall: String?,
    var utfallId: String?,
    var hjemler: List<String>?,
    var hjemmelIdList: List<String>?,
    var utfoerendeSaksbehandler: String,
    var kvalitetsvurderingId: UUID,
    var avsluttetAvSaksbehandler: LocalDateTime?,
    val created: LocalDateTime,
    var modified: LocalDateTime
)

fun Saksdata.toSaksdataView(): SaksdataView {
    return SaksdataView(
        id = id,
        sakenGjelder = sakenGjelder,
        sakstype = sakstype?.id,
        sakstypeId = sakstype?.id,
        tema = tema?.id,
        temaId = tema?.id,
        ytelse = ytelse?.id,
        ytelseId = ytelse?.id,
        mottattVedtaksinstans = mottattVedtaksinstans,
        vedtaksinstansEnhet = vedtaksinstansEnhet,
        mottattKlageinstans = mottattKlageinstans,
        utfall = utfall?.id,
        utfallId = utfall?.id,
        hjemler = hjemler?.map { it.id } ?: emptyList(),
        hjemmelIdList = hjemler?.map { it.id } ?: emptyList(),
        utfoerendeSaksbehandler = utfoerendeSaksbehandler,
        kvalitetsvurderingId = kvalitetsvurdering.id,
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        created = created,
        modified = modified
    )
}