package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface KvalitetsvurderingV3Repository : JpaRepository<KvalitetsvurderingV3, UUID>