package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3

fun getMangelfulltQueryV3(mangelfullt: List<String>): String {
    if (mangelfullt.isEmpty()) {
        return ""
    }

    val queryParts = mutableListOf<String?>()

    queryParts += getSaerregelverketQuery(mangelfullt)
    queryParts += getSaksbehandlingsreglerQuery(mangelfullt)
    queryParts += getTrygdemedisinQuery(mangelfullt)

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

private fun getSaerregelverketQuery(mangelfullt: List<String>) =
    if ("saerregelverket" in mangelfullt) {
        """
                k.saerregelverk = '${KvalitetsvurderingV3.Radiovalg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getSaksbehandlingsreglerQuery(mangelfullt: List<String>) =
    if ("saksbehandlingsreglene" in mangelfullt) {
        """
                k.saksbehandlingsregler = '${KvalitetsvurderingV3.Radiovalg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getTrygdemedisinQuery(mangelfullt: List<String>) =
    if ("rol" in mangelfullt) {
        """
                k.brukAvRaadgivendeLege = '${KvalitetsvurderingV3.RadiovalgRaadgivendeLege.MANGELFULLT.name}'
            """.trimIndent()
    } else null