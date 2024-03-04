package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2

fun getMangelfulltQueryV2(mangelfullt: List<String>): String {
    if (mangelfullt.isEmpty()) {
        return ""
    }

    val queryParts = mutableListOf<String?>()

    queryParts += getForberedelsenQuery(mangelfullt)
    queryParts += getUtredningenQuery(mangelfullt)
    queryParts += getVedtaketQuery(mangelfullt)
    queryParts += getROLQuery(mangelfullt)

    var query = ""

    if (queryParts.filterNotNull().isNotEmpty()) {
        query += " AND ( "
    } else {
        return ""
    }

    for ((index, q) in queryParts.filterNotNull().withIndex()) {
        query += if (index == 0) {
            q
        } else {
            " OR $q"
        }
    }

    query += " ) "

    return query
}

private fun getForberedelsenQuery(mangelfullt: List<String>) =
    if ("forberedelsen" in mangelfullt) {
        """
                kvalitetsvurderingV2.klageforberedelsen = '${KvalitetsvurderingV2.Radiovalg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getUtredningenQuery(mangelfullt: List<String>) =
    if ("utredningen" in mangelfullt) {
        """
                kvalitetsvurderingV2.utredningen = '${KvalitetsvurderingV2.Radiovalg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getVedtaketQuery(mangelfullt: List<String>) =
    if ("vedtaket" in mangelfullt) {
        """
                kvalitetsvurderingV2.vedtaket = '${KvalitetsvurderingV2.Radiovalg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getROLQuery(mangelfullt: List<String>) =
    if ("rol" in mangelfullt) {
        """
                kvalitetsvurderingV2.brukAvRaadgivendeLege = '${KvalitetsvurderingV2.RadiovalgRaadgivendeLege.MANGELFULLT.name}'
            """.trimIndent()
    } else null
