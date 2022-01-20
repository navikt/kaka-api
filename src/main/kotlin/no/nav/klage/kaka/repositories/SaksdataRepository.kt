package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
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

    /** Dates are exclusive */
    fun findByTilknyttetEnhetInAndAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        enhetIdList: List<String>,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    /** Dates are exclusive */
    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<Saksdata>

    @EntityGraph(attributePaths = ["kvalitetsvurdering", "registreringshjemler"])
    fun findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanEqualOrderByCreated(
        toDateTime: LocalDateTime
    ): List<Saksdata>
}