package no.nav.klage.kaka.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.*

@Embeddable
data class KvalitetsvurderingReference(
    @Column(name = "kvalitetsvurdering_id")
    var id: UUID,
    @Column(name = "kvalitetsvurdering_version")
    var version: Int,
)