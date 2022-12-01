package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.KvalitetsvurderingV1
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface KvalitetsvurderingRepository : JpaRepository<KvalitetsvurderingV1, UUID>