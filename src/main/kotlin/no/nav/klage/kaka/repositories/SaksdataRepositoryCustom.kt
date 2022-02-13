package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Saksdata
import java.time.LocalDateTime

interface SaksdataRepositoryCustom {

    fun findForVedtaksinstansleder(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<Saksdata>
}