package no.nav.klage.kaka.domain

import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class KvalitetsvurderingReference(
    @Column(name = "kvalitetsvurdering_id")
    var id: UUID,
    @Column(name = "kvalitetsvurdering_version")
    var version: Int,
)