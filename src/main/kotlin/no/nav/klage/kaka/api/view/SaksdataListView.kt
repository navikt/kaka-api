package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDateTime
import java.util.*

data class SaksdataListView(
    val searchHits: List<SaksdataSearchHitView>
)

data class SaksdataSearchHitView (
    val id: UUID,
    var sakenGjelder: String? = null,
    var sakstype: String? = null,
    var tema: String? = null,
    var utfall: String? = null,
    var hjemler: List<String> = emptyList(),
    var avsluttetAvSaksbehandler: LocalDateTime? = null,
    val created: LocalDateTime,
    var modified: LocalDateTime
)

fun Saksdata.toSaksdataSearchHitView(): SaksdataSearchHitView {
    return SaksdataSearchHitView(
        id = id,
        sakenGjelder = klager,
        sakstype = sakstype?.id,
        tema = tema?.id,
        utfall = utfall?.id,
        hjemler = hjemler.map { it.id },
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        created = created,
        modified = modified
    )
}