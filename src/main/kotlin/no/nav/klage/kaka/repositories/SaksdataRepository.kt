package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface SaksdataRepository : JpaRepository<Saksdata, UUID> {

    fun findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerIsNullOrderByCreated(saksbehandlerIdent: String): List<Saksdata>

    fun findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByCreated(
        saksbehandlerIdent: String,
        fromDate: LocalDateTime
    ): List<Saksdata>
}