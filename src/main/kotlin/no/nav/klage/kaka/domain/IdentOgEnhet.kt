package no.nav.klage.kaka.domain

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class IdentOgEnhet(
    @Column(name = "saksbehandlerident")
    val saksbehandlerident: String? = null,
    @Column(name = "enhet")
    val enhet: String? = null
)
