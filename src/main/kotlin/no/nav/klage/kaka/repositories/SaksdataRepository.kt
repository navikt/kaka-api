package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
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

    fun findByTilbakekrevingIsFalse(): List<Saksdata>

    //TODO: Delete after run
    fun findByAvsluttetAvSaksbehandlerIsNullAndKvalitetsvurderingReferenceVersion(kvalitetsvurderingVersion: Int): List<Saksdata>
}