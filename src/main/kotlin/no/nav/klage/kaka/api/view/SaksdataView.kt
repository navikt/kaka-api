package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.Hjemmel
import no.nav.klage.kaka.domain.kodeverk.Sakstype
import no.nav.klage.kaka.domain.kodeverk.Tema
import no.nav.klage.kaka.domain.kodeverk.Utfall
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SaksdataView (
    val id: UUID,
    var klager: String? = null,
    var sakstype: Sakstype? = null,
    var tema: Tema? = null,
    var mottattVedtaksinstans: LocalDate? = null,
    var vedtaksinstansEnhet: String? = null,
    var mottattKlageinstans: LocalDate? = null,
    var utfall: Utfall? = null,
    var hjemler: MutableSet<Hjemmel> = mutableSetOf(),
    var utfoerendeSaksbehandler: String,
    var kvalitetsvurderingId: UUID,
    var avsluttetAvSaksbehandler: LocalDateTime? = null,
    val created: LocalDateTime,
    var modified: LocalDateTime
)

fun Saksdata.toSaksdataView(): SaksdataView {
    return SaksdataView(
        id,
        klager,
        sakstype,
        tema,
        mottattVedtaksinstans,
        vedtaksinstansEnhet,
        mottattKlageinstans,
        utfall,
        hjemler,
        utfoerendeSaksbehandler,
        kvalitetsvurdering.id,
        avsluttetAvSaksbehandler,
        created,
        modified
    )
}