package no.nav.klage.kaka.api.view

data class UserData(val ident: String, val navn: Navn, val klageenheter: List<KodeDto>) {
    data class Navn(
        val fornavn: String? = null,
        val etternavn: String? = null,
        val sammensattNavn: String?,
    )
}