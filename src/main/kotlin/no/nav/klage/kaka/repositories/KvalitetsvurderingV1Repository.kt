package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface KvalitetsvurderingV1Repository : JpaRepository<KvalitetsvurderingV1, UUID>