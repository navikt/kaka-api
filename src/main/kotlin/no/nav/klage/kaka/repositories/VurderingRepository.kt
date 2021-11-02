package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.Vurdering
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VurderingRepository : JpaRepository<Vurdering, UUID> {
}