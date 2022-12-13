package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface KvalitetsvurderingV2Repository : JpaRepository<KvalitetsvurderingV2, UUID>