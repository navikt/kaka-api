package no.nav.klage.kaka.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kodeverk.Type
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

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

    private fun privateFindByAvsluttetAvSaksbehandlerBetween(
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
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
        """,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .resultList
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreatedV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): Set<QueryResultV1> {
        return privateFindByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            saksbehandler = saksbehandler,
            version = 1,
        ).mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreatedV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): Set<QueryResultV2> {
        return privateFindByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            saksbehandler = saksbehandler,
            version = 2,
        ).mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    private fun privateFindByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
        version: Int,
    ): List<Array<*>> {
        val resultList = entityManager.createQuery(
            """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV$version k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.utfoerendeSaksbehandler = :saksbehandler
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
        """,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("saksbehandler", saksbehandler)
            .resultList
        return resultList
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenAndEnhetAndNotUtfoerendeSaksbehandlerOrderByCreatedV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        enhet: String,
        saksbehandler: String
    ): Set<QueryResultV1> {
        return privateFindByAvsluttetAvSaksbehandlerBetweenAndEnhetAndNotUtfoerendeSaksbehandlerOrderByCreated(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            enhet = enhet,
            saksbehandler = saksbehandler,
            version = 1,
        ).mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findByAvsluttetAvSaksbehandlerBetweenAndEnhetAndNotUtfoerendeSaksbehandlerOrderByCreatedV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        enhet: String,
        saksbehandler: String
    ): Set<QueryResultV2> {
        return privateFindByAvsluttetAvSaksbehandlerBetweenAndEnhetAndNotUtfoerendeSaksbehandlerOrderByCreated(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            enhet = enhet,
            saksbehandler = saksbehandler,
            version = 2,
        ).mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    private fun privateFindByAvsluttetAvSaksbehandlerBetweenAndEnhetAndNotUtfoerendeSaksbehandlerOrderByCreated(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        enhet: String,
        saksbehandler: String,
        version: Int,
    ): List<Array<*>> {
        val resultList = entityManager.createQuery(
            """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV$version k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.utfoerendeSaksbehandler <> :saksbehandler
            AND s.tilknyttetEnhet = $enhet
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
        """,
            Array::class.java
        )
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("saksbehandler", saksbehandler)
            .resultList
        return resultList
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

    override fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreatedV1(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): Set<QueryResultV1> {
        return privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
            enhet = enhet,
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            saksbehandlerIdentList = saksbehandlerIdentList,
            version = 1,
        ).mapToSet { QueryResultV1(it[0] as Saksdata, it[1] as KvalitetsvurderingV1) }
    }

    override fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreatedV2(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): Set<QueryResultV2> {
        return privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
            enhet = enhet,
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
            saksbehandlerIdentList = saksbehandlerIdentList,
            version = 2,
        ).mapToSet { QueryResultV2(it[0] as Saksdata, it[1] as KvalitetsvurderingV2) }
    }

    private fun privateFindByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>,
        version: Int
    ): List<Array<*>> {
        return entityManager.createQuery(
            """
            SELECT s, k
            FROM Saksdata s
             LEFT JOIN FETCH KvalitetsvurderingV$version k on s.kvalitetsvurderingReference.id = k.id
             LEFT JOIN FETCH s.registreringshjemler h
            WHERE s.kvalitetsvurderingReference.version = $version
            AND s.tilknyttetEnhet = :enhet
            AND s.avsluttetAvSaksbehandler BETWEEN :fromDateTime AND :toDateTime
            AND s.utfoerendeSaksbehandler IN :saksbehandlerIdentList
            ORDER BY s.created
        """,
            Array::class.java
        )
            .setParameter("enhet", enhet)
            .setParameter("fromDateTime", fromDateTime)
            .setParameter("toDateTime", toDateTime)
            .setParameter("saksbehandlerIdentList", saksbehandlerIdentList)
            .resultList
    }

    override fun findForVedtaksinstanslederV1(
        vedtaksinstansEnhet: String,
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

    override fun findForVedtaksinstanslederV2(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
    ): Set<QueryResultV2> {

        val query = """
            SELECT s, k 
            FROM Saksdata s 
              LEFT JOIN FETCH KvalitetsvurderingV2 k on s.kvalitetsvurderingReference.id = k.id 
              LEFT JOIN FETCH s.registreringshjemler h
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
}