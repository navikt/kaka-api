package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SaksdataView (
    val id: UUID,
    var sakenGjelder: String?,
    var sakstypeId: String?,
    var ytelseId: String?,
    var mottattVedtaksinstans: LocalDate?,
    var vedtaksinstansEnhet: String?,
    var mottattKlageinstans: LocalDate?,
    var utfallId: String?,
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
        sakstypeId = sakstype?.id,
        ytelseId = ytelse?.id,
        mottattVedtaksinstans = mottattVedtaksinstans,
        vedtaksinstansEnhet = vedtaksinstansEnhet,
        mottattKlageinstans = mottattKlageinstans,
        utfallId = utfall?.id,
        hjemmelIdList = hjemler?.map { it.id } ?: emptyList(),
        utfoerendeSaksbehandler = utfoerendeSaksbehandler,
        kvalitetsvurderingId = kvalitetsvurdering.id,
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        created = created,
        modified = modified
    )
}