package no.nav.klage.kaka.clients.pdl

import no.nav.klage.kaka.clients.pdl.graphql.PdlPerson

data class Person(
    val foedselsnr: String,
    val fornavn: String?,
    val mellomnavn: String?,
    val etternavn: String?,
    val sammensattNavn: String?,
    val beskyttelsesbehov: Beskyttelsesbehov?,
    val kjoenn: String?,
    val sivilstand: Sivilstand?
) {
    fun harBeskyttelsesbehovFortrolig() = beskyttelsesbehov == Beskyttelsesbehov.FORTROLIG

    fun harBeskyttelsesbehovStrengtFortrolig() =
        beskyttelsesbehov == Beskyttelsesbehov.STRENGT_FORTROLIG || beskyttelsesbehov == Beskyttelsesbehov.STRENGT_FORTROLIG_UTLAND

    fun settSammenNavn(): String {
        return if (mellomnavn != null) {
            "$fornavn $mellomnavn $etternavn"
        } else {
            "$fornavn $etternavn"
        }
    }
}

data class Sivilstand(val type: PdlPerson.Sivilstand.SivilstandType, val foedselsnr: String)

enum class Beskyttelsesbehov {
    STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG
}
