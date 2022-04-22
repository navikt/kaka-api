package no.nav.klage.kaka.clients.norg2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Norg2Enhet(
    val aktiveringsdato: String,
    val antallRessurser: Int,
    val enhetId: Int,
    val enhetNr: String,
    val kanalstrategi: String?,
    val navn: String,
    val nedleggelsesdato: String?,
    val oppgavebehandler: Boolean,
    val orgNivaa: String,
    val orgNrTilKommunaltNavKontor: String?,
    val organisasjonsnummer: String?,
    val sosialeTjenester: String?,
    val status: String,
    val type: String,
    val underAvviklingDato: String?,
    val underEtableringDato: String,
)