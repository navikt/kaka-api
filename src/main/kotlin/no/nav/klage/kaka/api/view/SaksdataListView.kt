package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDateTime
import java.util.*

data class SaksdataListView(
    val searchHits: List<SaksdataSearchHitView>
)

data class SaksdataSearchHitView (
    val id: UUID,
    var sakenGjelder: String?,
    var sakstype: String?,
    var sakstypeId: String?,
    var tema: String?,
    var temaId: String?,
    var ytelseId: String?,
    var utfall: String?,
    var utfallId: String?,
    var hjemler: List<String>?,
    var hjemmelIdList: List<String>?,
    var avsluttetAvSaksbehandler: LocalDateTime?,
    val created: LocalDateTime,
    var modified: LocalDateTime
)

fun Saksdata.toSaksdataSearchHitView(): SaksdataSearchHitView {
    return SaksdataSearchHitView(
        id = id,
        sakenGjelder = sakenGjelder,
        sakstype = sakstype?.id,
        sakstypeId = sakstype?.id,
        tema = tema?.id,
        temaId = tema?.id,
        ytelseId = ytelse?.id,
        utfall = utfall?.id,
        utfallId = utfall?.id,
        hjemler = hjemler?.map { it.id } ?: emptyList(),
        hjemmelIdList = hjemler?.map { it.id } ?: emptyList(),
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        created = created,
        modified = modified
    )
}