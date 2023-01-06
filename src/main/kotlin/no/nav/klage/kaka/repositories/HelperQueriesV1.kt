package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1

fun getMangelfulltQueryV1(mangelfullt: List<String>): String {
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
                k.klageforberedelsenRadioValg = '${KvalitetsvurderingV1.RadioValg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getUtredningenQuery(mangelfullt: List<String>) =
    if ("utredningen" in mangelfullt) {
        """
                k.utredningenRadioValg = '${KvalitetsvurderingV1.RadioValg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getVedtaketQuery(mangelfullt: List<String>) =
    if ("vedtaket" in mangelfullt) {
        """
                k.vedtaketRadioValg = '${KvalitetsvurderingV1.RadioValg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getROLQuery(mangelfullt: List<String>) =
    if ("rol" in mangelfullt) {
        """
                k.brukAvRaadgivendeLegeRadioValg = '${KvalitetsvurderingV1.RadioValgRaadgivendeLege.MANGELFULLT.name}'
            """.trimIndent()
    } else null

fun getKommentarerQueryV1(
    kommentarer: List<String>,
): String {
    if (kommentarer.isEmpty()) {
        return ""
    }

    var query = "AND ("

    if ("utredningen" in kommentarer) {
        query += """
                    k.utredningenAvMedisinskeForholdText IS NOT NULL OR
                    k.utredningenAvInntektsforholdText IS NOT NULL OR
                    k.utredningenAvArbeidText IS NOT NULL OR
                    k.arbeidsrettetBrukeroppfoelgingText IS NOT NULL OR
                    k.utredningenAvAndreAktuelleForholdISakenText IS NOT NULL OR
                    k.utredningenAvEoesProblematikkText IS NOT NULL OR
                    k.veiledningFraNavText IS NOT NULL
            """.trimIndent()
    }

    if ("avvik" in kommentarer) {
        if (query.length > 10) {
            query += " OR "
        }
        query += """
                    k.betydeligAvvikText IS NOT NULL
            """.trimIndent()
    }

    if ("opplaering" in kommentarer) {
        if (query.length > 10) {
            query += " OR "
        }
        query += """
                    k.brukIOpplaeringText IS NOT NULL
            """.trimIndent()
    }

    return "$query)"
}