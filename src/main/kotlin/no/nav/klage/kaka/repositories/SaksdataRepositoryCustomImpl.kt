package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.RadioValg.MANGELFULLT
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.RadioValgRaadgivendeLege
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kodeverk.Type
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SaksdataRepositoryCustomImpl : SaksdataRepositoryCustom {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    data class ResultV1(
        val saksdata: Saksdata,
        val kvalitetsvurdering: KvalitetsvurderingV1,
    )

    data class ResultV2(
        val saksdata: Saksdata,
        val kvalitetsvurdering: KvalitetsvurderingV2,
    )

    override fun findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<ResultV1> {
        return entityManager.createQuery(
            """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV1 k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.kvalitetsvurderingReference.version = 1
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            ORDER BY s.created
        """,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .resultList
            .map { ResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    fun findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<ResultV2> {
        return entityManager.createQuery(
            """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV2 k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.kvalitetsvurderingReference.version = 2
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            ORDER BY s.created
        """,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .resultList
            .map { ResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    override fun findForVedtaksinstansleder(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<Saksdata> {

        val query = """
            SELECT DISTINCT s FROM Saksdata s LEFT JOIN FETCH KvalitetsvurderingV1 k on s.kvalitetsvurderingReference.id = k.id LEFT JOIN FETCH s.registreringshjemler h
                WHERE s.vedtaksinstansEnhet = :vedtaksinstansEnhet
                AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
                AND s.sakstype = :sakstype
                ${getMangelfulltQuery(mangelfullt)}
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

    private fun getMangelfulltQuery(mangelfullt: List<String>): String {
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
                k.klageforberedelsenRadioValg = '${MANGELFULLT.name}'
            """.trimIndent()
        } else null

    private fun getUtredningenQuery(mangelfullt: List<String>) =
        if ("utredningen" in mangelfullt) {
            """
                k.utredningenRadioValg = '${MANGELFULLT.name}'
            """.trimIndent()
        } else null

    private fun getVedtaketQuery(mangelfullt: List<String>) =
        if ("vedtaket" in mangelfullt) {
            """
                k.vedtaketRadioValg = '${MANGELFULLT.name}'
            """.trimIndent()
        } else null

    private fun getROLQuery(mangelfullt: List<String>) =
        if ("rol" in mangelfullt) {
            """
                k.brukAvRaadgivendeLegeRadioValg = '${RadioValgRaadgivendeLege.MANGELFULLT.name}'
            """.trimIndent()
        } else null

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