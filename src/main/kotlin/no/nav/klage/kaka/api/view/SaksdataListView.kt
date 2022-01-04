package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDateTime
import java.util.*

data class SaksdataListView(
    val searchHits: List<SaksdataSearchHitView>
)

data class SaksdataSearchHitView(
    val id: UUID,
    var sakenGjelder: String?,
    var sakstypeId: String?,
    var ytelseId: String?,
    var utfallId: String?,
    var hjemmelIdList: List<String>,
    var avsluttetAvSaksbehandler: LocalDateTime?,
    val sourceId: String,
    val created: LocalDateTime,
    var modified: LocalDateTime
)

fun Saksdata.toSaksdataSearchHitView(): SaksdataSearchHitView {
    return SaksdataSearchHitView(
        id = id,
        sakenGjelder = sakenGjelder,
        sakstypeId = sakstype.id,
        ytelseId = ytelse?.id,
        utfallId = utfall?.id,
        hjemmelIdList = registreringshjemler?.map { it.id } ?: emptyList(),
        avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
        sourceId = source.id,
        created = created,
        modified = modified
    )
}