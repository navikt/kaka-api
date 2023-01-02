package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface SaksdataRepository : JpaRepository<Saksdata, UUID>, SaksdataRepositoryCustom {

    fun findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerIsNullOrderByCreated(saksbehandlerIdent: String): List<Saksdata>

    fun findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByModified(
        saksbehandlerIdent: String,
        fromDate: LocalDateTime
    ): List<Saksdata>

    fun findOneByKvalitetsvurderingReferenceId(kvalitetsvurderingId: UUID): Saksdata?

    //TODO: Disse brukes nå kun i exportServiceV2. De er feil der. Lag custom queries, og tilhørende tester.
    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        kvalitetsvurderingVersion: Int,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
        kvalitetsvurderingVersion: Int,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
        enhet: String,
        kvalitetsvurderingVersion: Int,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        enhet: String,
        kvalitetsvurderingVersion: Int,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerInOrderByCreated(
        enhet: String,
        kvalitetsvurderingVersion: Int,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): List<Saksdata>

    /** Dates are inclusive */
    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
        enhet: String,
        kvalitetsvurderingVersion: Int,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
        kvalitetsvurderingVersion: Int,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    @EntityGraph(attributePaths = ["registreringshjemler"])
    fun findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerOrderByCreated(
        kvalitetsvurderingVersion: Int,
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): List<Saksdata>
}