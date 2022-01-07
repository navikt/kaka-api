package no.nav.klage.kaka.domain.saksbehandler

import no.nav.klage.kodeverk.Enhet

data class SaksbehandlerPersonligInfo(
    val navIdent: String,
    val azureId: String,
    val fornavn: String,
    val etternavn: String,
    val sammensattNavn: String,
    val epost: String,
    val enhet: Enhet,
)