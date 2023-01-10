package no.nav.klage.kaka.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class IdentOgEnhet(
    @Column(name = "saksbehandlerident")
    val saksbehandlerident: String? = null,
    @Column(name = "enhet")
    val enhet: String? = null
)
