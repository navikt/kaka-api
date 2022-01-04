package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SaksdataView(
    val id: UUID,
    val sakenGjelder: String?,
    val sakstypeId: String?,
    val ytelseId: String?,
    val mottattVedtaksinstans: LocalDate?,
    val vedtaksinstansEnhet: String?,
    val mottattKlageinstans: LocalDate?,
    val utfallId: String?,
    val hjemmelIdList: List<String>,
    val utfoerendeSaksbehandler: String,
    val tilknyttetEnhet: String,
    val kvalitetsvurderingId: UUID,
    val avsluttetAvSaksbehandler: LocalDateTime?,
    val sourceId: String,
    val created: LocalDateTime,
    val modified: LocalDateTime
)

fun Saksdata.toSaksdataView(): SaksdataView {
    return SaksdataView(
        id = id,
        sakenGjelder = sakenGjelder,
        sakstypeId = sakstype.id,
        ytelseId = ytelse?.id,
        mottattVedtaksinstans = mottattVedtaksinstans,
        vedtaksinstansEnhet = vedtaksinstansEnhet,
        mottattKlageinstans = mottattKlageinstans,
        utfallId = utfall?.id,
        hjemmelIdList = registreringshjemler?.map { it.id } ?: emptyList(),
        utfoerendeSaksbehandler = utfoerendeSaksbehandler,
        tilknyttetEnhet = tilknyttetEnhet,
        kvalitetsvurderingId = kvalitetsvurdering.id,
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        sourceId = source.id,
        created = created,
        modified = modified
    )
}

data class SaksdataInput(val tilknyttetEnhet: String? = null)