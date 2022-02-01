package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kodeverk.Type
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface SaksdataRepository : JpaRepository<Saksdata, UUID> {

    fun findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerIsNullOrderByCreated(saksbehandlerIdent: String): List<Saksdata>

    fun findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByModified(
        saksbehandlerIdent: String,
        fromDate: LocalDateTime
    ): List<Saksdata>

    fun findOneByKvalitetsvurderingId(kvalitetsvurderingId: UUID): Saksdata?

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerInOrderByCreated(
        enhet: String,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
        enhet: String,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
        toDateTime: LocalDateTime
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByVedtaksinstansEnhetAndAvsluttetAvSaksbehandlerBetweenAndSakstypeOrderByCreated(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        sakstype: Type = Type.KLAGE
    ): List<Saksdata>
}