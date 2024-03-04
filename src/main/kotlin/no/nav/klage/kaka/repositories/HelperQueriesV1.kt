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
                kvalitetsvurderingV1.klageforberedelsenRadioValg = '${KvalitetsvurderingV1.RadioValg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getUtredningenQuery(mangelfullt: List<String>) =
    if ("utredningen" in mangelfullt) {
        """
                kvalitetsvurderingV1.utredningenRadioValg = '${KvalitetsvurderingV1.RadioValg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getVedtaketQuery(mangelfullt: List<String>) =
    if ("vedtaket" in mangelfullt) {
        """
                kvalitetsvurderingV1.vedtaketRadioValg = '${KvalitetsvurderingV1.RadioValg.MANGELFULLT.name}'
            """.trimIndent()
    } else null

private fun getROLQuery(mangelfullt: List<String>) =
    if ("rol" in mangelfullt) {
        """
                kvalitetsvurderingV1.brukAvRaadgivendeLegeRadioValg = '${KvalitetsvurderingV1.RadioValgRaadgivendeLege.MANGELFULLT.name}'
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
                    kvalitetsvurderingV1.utredningenAvMedisinskeForholdText IS NOT NULL OR
                    kvalitetsvurderingV1.utredningenAvInntektsforholdText IS NOT NULL OR
                    kvalitetsvurderingV1.utredningenAvArbeidText IS NOT NULL OR
                    kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelgingText IS NOT NULL OR
                    kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISakenText IS NOT NULL OR
                    kvalitetsvurderingV1.utredningenAvEoesProblematikkText IS NOT NULL OR
                    kvalitetsvurderingV1.veiledningFraNavText IS NOT NULL
            """.trimIndent()
    }

    if ("avvik" in kommentarer) {
        if (query.length > 10) {
            query += " OR "
        }
        query += """
                    kvalitetsvurderingV1.betydeligAvvikText IS NOT NULL
            """.trimIndent()
    }

    if ("opplaering" in kommentarer) {
        if (query.length > 10) {
            query += " OR "
        }
        query += """
                    kvalitetsvurderingV1.brukIOpplaeringText IS NOT NULL
            """.trimIndent()
    }

    return "$query)"
}