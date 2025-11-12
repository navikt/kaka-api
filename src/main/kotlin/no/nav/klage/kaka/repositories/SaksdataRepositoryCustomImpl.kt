package no.nav.klage.kaka.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import no.nav.klage.kaka.domain.vedtaksinstansgruppeMap
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.ytelse.Ytelse
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * TODO Check queries again when we upgrade to Spring Boot 3 and Hibernate 6
 * Might not need "distinct", or we can use a resultsTransformer directly.
 * https://thorben-janssen.com/hibernate-resulttransformer/
 */
@Repository
class SaksdataRepositoryCustomImpl : SaksdataRepositoryCustom {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    data class QueryResultV1(
        val saksdata: Saksdata,
        val kvalitetsvurdering: KvalitetsvurderingV1,
    )

    data class QueryResultV2(
        val saksdata: Saksdata,
        val kvalitetsvurdering: KvalitetsvurderingV2,
    )

    data class QueryResultV3(
        val saksdata: Saksdata,
        val kvalitetsvurdering: KvalitetsvurderingV3,
    )

    private inline fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R): Set<R> {
        return mapTo(HashSet(), transform)
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<QueryResultV1> {
        return privateFindByAvsluttetAvSaksbehandlerBetween(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            version = 1,
        ).mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<QueryResultV2> {
        return privateFindByAvsluttetAvSaksbehandlerBetween(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            version = 2,
        ).mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenV3(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<QueryResultV3> {
        return privateFindByAvsluttetAvSaksbehandlerBetween(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            version = 3,
        ).mapToSet { QueryResultV3(it[0] as Saksdata, it[1] as KvalitetsvurderingV3) }
    }

    override fun findByQueryParamsV1(
        fromDate: LocalDate,
        toDate: LocalDate,
        tilbakekreving: String,
        klageenheter: List<String>?,
        vedtaksinstansgrupper: List<Int>?,
        enheter: List<String>?,
        types: List<String>?,
        ytelser: List<String>?,
        utfall: List<String>?,
        hjemler: List<String>?
    ): Set<QueryResultV1> {
        return privateFindByQueryParams(
            version = 1,
            fromDate = fromDate,
            toDate = toDate,
            tilbakekreving = tilbakekreving,
            klageenheter = klageenheter,
            vedtaksinstansgrupper = vedtaksinstansgrupper,
            enheter = enheter,
            types = types,
            ytelser = ytelser,
            utfall = utfall,
            hjemler = hjemler
        ).mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findByQueryParamsV2(
        fromDate: LocalDate,
        toDate: LocalDate,
        tilbakekreving: String,
        klageenheter: List<String>?,
        vedtaksinstansgrupper: List<Int>?,
        enheter: List<String>?,
        types: List<String>?,
        ytelser: List<String>?,
        utfall: List<String>?,
        hjemler: List<String>?
    ): Set<QueryResultV2> {
        return privateFindByQueryParams(
            version = 2,
            fromDate = fromDate,
            toDate = toDate,
            tilbakekreving = tilbakekreving,
            klageenheter = klageenheter,
            vedtaksinstansgrupper = vedtaksinstansgrupper,
            enheter = enheter,
            types = types,
            ytelser = ytelser,
            utfall = utfall,
            hjemler = hjemler
        ).mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    override fun findByQueryParamsV3(
        fromDate: LocalDate,
        toDate: LocalDate,
        tilbakekreving: String,
        klageenheter: List<String>?,
        vedtaksinstansgrupper: List<Int>?,
        enheter: List<String>?,
        types: List<String>?,
        ytelser: List<String>?,
        utfall: List<String>?,
        hjemler: List<String>?
    ): Set<QueryResultV3> {
        return privateFindByQueryParams(
            version = 3,
            fromDate = fromDate,
            toDate = toDate,
            tilbakekreving = tilbakekreving,
            klageenheter = klageenheter,
            vedtaksinstansgrupper = vedtaksinstansgrupper,
            enheter = enheter,
            types = types,
            ytelser = ytelser,
            utfall = utfall,
            hjemler = hjemler
        ).mapToSet { QueryResultV3(it[0] as Saksdata, it[1] as KvalitetsvurderingV3) }
    }

    private fun privateFindByQueryParams(
        version: Int,
        fromDate: LocalDate,
        toDate: LocalDate,
        tilbakekreving: String,
        klageenheter: List<String>?,
        vedtaksinstansgrupper: List<Int>?,
        enheter: List<String>?,
        types: List<String>?,
        ytelser: List<String>?,
        utfall: List<String>?,
        hjemler: List<String>?
    ): List<Array<*>> {
        val (hjemlerQuery, registreringshjemmelSet) = getHjemlerQuery(hjemler)
        val (typesQuery, typeSet) = getTypesQuery(types)
        val (ytelseQuery, ytelseSet) = getYtelserQuery(ytelser)
        val (utfallQuery, utfallSet) = getUtfallQuery(utfall)

        val jpaQuery = """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV$version k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler
             ${getPossibleHjemlerJoins(version)}
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            ${getKlageenheterQuery(klageenheter)}
            ${getVedtaksinstansgrupperQuery(vedtaksinstansgrupper)}
            ${getEnheterQuery(enheter)}
            $typesQuery
            $ytelseQuery
            $utfallQuery
            $hjemlerQuery
            ${getTilbakekrevingQuery(tilbakekreving)}
        """

        val typedQuery = entityManager.createQuery(
            jpaQuery,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDate.atStartOfDay())
            .setParameter("toDateTime", toDate.atTime(LocalTime.MAX))

        if (!klageenheter.isNullOrEmpty()) {
            typedQuery.setParameter("klageenheter", klageenheter)
        }
        if (!enheter.isNullOrEmpty()) {
            typedQuery.setParameter("enheter", enheter)
        }
        if (typeSet.isNotEmpty()) {
            typedQuery.setParameter("types", typeSet)
        }
        if (ytelseSet.isNotEmpty()) {
            typedQuery.setParameter("ytelser", ytelseSet)
        }
        if (utfallSet.isNotEmpty()) {
            typedQuery.setParameter("utfall", utfallSet)
        }
        if (registreringshjemmelSet.isNotEmpty()) {
            registreringshjemmelSet.forEach {
                typedQuery.setParameter("h_${it.id}", it)
            }
        }

        return typedQuery.resultList
    }

    private fun getTilbakekrevingQuery(tilbakekreving: String): String {
        return when (tilbakekreving) {
            "include" -> ""
            "exclude" -> "AND s.tilbakekreving = false"
            "only" -> "AND s.tilbakekreving = true"
            else -> ""
        }
    }

    private fun getHjemlerQuery(hjemler: List<String>?): Pair<String, Set<Registreringshjemmel>> {
        val hjemlerToWorkWith = hjemler?.mapToSet { Registreringshjemmel.of(it) } ?: emptySet()

        return if (hjemlerToWorkWith.isNotEmpty()) {
            hjemlerToWorkWith.joinToString(
                prefix = "AND (",
                postfix = ")",
                separator = " OR "
            ) { ":h_${it.id} member s.registreringshjemler" } to hjemlerToWorkWith
        } else {
            "" to emptySet()
        }
    }

    private fun getKlageenheterQuery(klageenheter: List<String>?): String {
        if (klageenheter.isNullOrEmpty()) {
            return ""
        }
        return "AND s.tilknyttetEnhet IN :klageenheter"
    }

    private fun getEnheterQuery(enheter: List<String>?): String {
        if (enheter.isNullOrEmpty()) {
            return ""
        }
        return "AND s.vedtaksinstansEnhet IN :enheter"
    }

    private fun getTypesQuery(types: List<String>?): Pair<String, Set<Type>> {
        if (types.isNullOrEmpty()) {
            return "" to emptySet()
        }
        return "AND s.sakstype IN :types" to types.mapToSet { Type.of(it) }
    }

    private fun getYtelserQuery(ytelser: List<String>?): Pair<String, Set<Ytelse>> {
        if (ytelser.isNullOrEmpty()) {
            return "" to emptySet()
        }
        return "AND s.ytelse IN :ytelser" to ytelser.mapToSet { Ytelse.of(it) }
    }

    private fun getUtfallQuery(utfall: List<String>?): Pair<String, Set<Utfall>> {
        if (utfall.isNullOrEmpty()) {
            return "" to emptySet()
        }
        return "AND s.utfall IN :utfall" to utfall.mapToSet { Utfall.of(it) }
    }

    private fun getVedtaksinstansgrupperQuery(vedtaksinstansgrupper: List<Int>?): String {
        if (vedtaksinstansgrupper.isNullOrEmpty()) {
            return ""
        }

        val firstTwoLettersOfVedtaksinstansList = vedtaksinstansgrupper.flatMap { vedtaksinstansgruppeId ->
            vedtaksinstansgruppeMap.entries.filter { it.value.id == vedtaksinstansgruppeId }
        }.map { it.key }

        return if (firstTwoLettersOfVedtaksinstansList.isNotEmpty()) {
            firstTwoLettersOfVedtaksinstansList.joinToString(
                prefix = "AND (",
                postfix = ")",
                separator = " OR "
            ) { "s.vedtaksinstansEnhet like concat('$it', '%')" }
        } else {
            ""
        }
    }

    private fun privateFindByAvsluttetAvSaksbehandlerBetween(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        version: Int,
    ): List<Array<*>> {
        val query = """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV$version k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler
             ${getPossibleHjemlerJoins(version)}
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
        """

        return entityManager.createQuery(
            query,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .resultList
    }

    override fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): Set<QueryResultV1> {
        return privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
            enhet = enhet,
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            version = 1,
        ).mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV2(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): Set<QueryResultV2> {
        return privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
            enhet = enhet,
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            version = 2,
        ).mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    override fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV3(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): Set<QueryResultV3> {
        return privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
            enhet = enhet,
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            version = 3,
        ).mapToSet { QueryResultV3(it[0] as Saksdata, it[1] as KvalitetsvurderingV3) }
    }

    private fun privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        version: Int,
    ): List<Array<*>> {
        return entityManager.createQuery(
            """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV$version k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler h
             ${getPossibleHjemlerJoins(version)}
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.tilknyttetEnhet = :enhet
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            ORDER BY s.created
        """,
            Array::class.java
        )
            .setParameter("enhet", enhet)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .resultList
    }

    override fun findForVedtaksinstanslederWithEnhetV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
        vedtaksinstansEnhet: String,
    ): Set<QueryResultV1> {
        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV1 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.vedtaksinstansEnhet = :vedtaksinstansEnhet
            AND s.kvalitetsvurderingReference.version = 1
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.sakstype = :sakstype
            ${getMangelfulltQueryV1(mangelfullt)}
            ${getKommentarerQueryV1(kommentarer)}
            ORDER BY s.created
        """

        return entityManager.createQuery(query, Array::class.java)
            .setParameter("vedtaksinstansEnhet", vedtaksinstansEnhet)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
            .mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findForVedtaksinstanslederWithEnhetV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        vedtaksinstansEnhet: String,
    ): Set<QueryResultV2> {

        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV2 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
              ${getPossibleHjemlerJoins(2)}
            WHERE s.vedtaksinstansEnhet = :vedtaksinstansEnhet
            AND s.kvalitetsvurderingReference.version = 2
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.sakstype = :sakstype
            ${getMangelfulltQueryV2(mangelfullt)}
            ORDER BY s.created
        """

        return entityManager.createQuery(query, Array::class.java)
            .setParameter("vedtaksinstansEnhet", vedtaksinstansEnhet)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
            .mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    override fun findForVedtaksinstanslederV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): Set<QueryResultV1> {
        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV1 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.kvalitetsvurderingReference.version = 1
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.sakstype = :sakstype
            ${getMangelfulltQueryV1(mangelfullt)}
            ${getKommentarerQueryV1(kommentarer)}
            ORDER BY s.created
        """

        return entityManager.createQuery(query, Array::class.java)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
            .mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findForVedtaksinstanslederV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
    ): Set<QueryResultV2> {

        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV2 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
              ${getPossibleHjemlerJoins(2)}
            WHERE s.kvalitetsvurderingReference.version = 2
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.sakstype = :sakstype
            ${getMangelfulltQueryV2(mangelfullt)}
            ORDER BY s.created
        """

        return entityManager.createQuery(query, Array::class.java)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
            .mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    override fun findForVedtaksinstanslederV3(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
    ): Set<QueryResultV3> {

        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV3 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
              ${getPossibleHjemlerJoins(3)}
            WHERE s.kvalitetsvurderingReference.version = 3
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.sakstype = :sakstype
            ${getMangelfulltQueryV3(mangelfullt)}
            ORDER BY s.created
        """

        return entityManager.createQuery(query, Array::class.java)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
            .mapToSet { QueryResultV3(it[0] as Saksdata, it[1] as KvalitetsvurderingV3) }
    }

    override fun findForVedtaksinstanslederWithEnhetV3(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        vedtaksinstansEnhet: String,
    ): Set<QueryResultV3> {

        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV3 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
              ${getPossibleHjemlerJoins(3)}
            WHERE s.vedtaksinstansEnhet = :vedtaksinstansEnhet
            AND s.kvalitetsvurderingReference.version = 3
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.sakstype = :sakstype
            ${getMangelfulltQueryV3(mangelfullt)}
            ORDER BY s.created
        """

        return entityManager.createQuery(query, Array::class.java)
            .setParameter("vedtaksinstansEnhet", vedtaksinstansEnhet)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("sakstype", Type.KLAGE)
            .resultList
            .mapToSet { QueryResultV3(it[0] as Saksdata, it[1] as KvalitetsvurderingV3) }
    }

    private fun getPossibleHjemlerJoins(version: Int): String {
        return if (version == 2) {
            """
                LEFT JOIN FETCH k.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList
                LEFT JOIN FETCH k.vedtaketLovbestemmelsenTolketFeilHjemlerList
                LEFT JOIN FETCH k.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList
                LEFT JOIN FETCH k.vedtaketFeilKonkretRettsanvendelseHjemlerList
                LEFT JOIN FETCH k.vedtaketBruktFeilHjemmelHjemlerList
                LEFT JOIN FETCH k.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList
            """
        } else if (version == 3) {
            """
                LEFT JOIN FETCH k.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList
                LEFT JOIN FETCH k.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList
                LEFT JOIN FETCH k.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList
                LEFT JOIN FETCH k.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList
                LEFT JOIN FETCH k.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList
                LEFT JOIN FETCH k.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList
            """
        } else {
            ""
        }
    }
}