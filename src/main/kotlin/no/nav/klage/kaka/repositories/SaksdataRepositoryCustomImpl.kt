package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Kvalitetsvurdering.RadioValg.MANGELFULLT
import no.nav.klage.kaka.domain.Kvalitetsvurdering.RadioValgRaadgivendeLege
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kodeverk.Type
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SaksdataRepositoryCustomImpl : SaksdataRepositoryCustom {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun findForVedtaksinstansleder(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<Saksdata> {
        val query = """
            SELECT DISTINCT s FROM Saksdata s JOIN FETCH s.kvalitetsvurdering k LEFT JOIN FETCH s.registreringshjemler h
                WHERE s.vedtaksinstansEnhet = :vedtaksinstansEnhet
                AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
                AND s.sakstype = :sakstype
                ${getForberedelsenQuery(mangelfullt)}
                ${getUtredningenQuery(mangelfullt)}
                ${getVedtaketQuery(mangelfullt)}
                ${getROLQuery(mangelfullt)}
                ${getKommentarerQuery(kommentarer)}
                ORDER BY s.created
        """

        return entityManager.createQuery(query, Saksdata::class.java)
            .setParameter("vedtaksinstansEnhet", vedtaksinstansEnhet)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
    }

    private fun getForberedelsenQuery(mangelfullt: List<String>) =
        if ("forberedelsen" in mangelfullt) {
            """
                AND k.klageforberedelsenRadioValg = '${MANGELFULLT.ordinal}'
            """.trimIndent()
        } else ""

    private fun getUtredningenQuery(mangelfullt: List<String>) =
        if ("utredningen" in mangelfullt) {
            """
                AND k.utredningenRadioValg = '${MANGELFULLT.ordinal}'
            """.trimIndent()
        } else ""

    private fun getVedtaketQuery(mangelfullt: List<String>) =
        if ("vedtaket" in mangelfullt) {
            """
                AND k.vedtaketRadioValg = '${MANGELFULLT.ordinal}'
            """.trimIndent()
        } else ""

    private fun getROLQuery(mangelfullt: List<String>) =
        if ("rol" in mangelfullt) {
            """
                AND k.brukAvRaadgivendeLegeRadioValg = '${RadioValgRaadgivendeLege.MANGELFULLT.ordinal}'
            """.trimIndent()
        } else ""

    private fun getKommentarerQuery(
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

        return query + ")"
    }
}