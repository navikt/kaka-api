package no.nav.klage.kaka.api.view

data class UserData(val ident: String, val navn: Navn, val klageenheter: List<KodeDto>, val roller: List<Rolle>) {
    data class Navn(
        val fornavn: String? = null,
        val etternavn: String? = null,
        val sammensattNavn: String?,
    )

    data class Rolle(
        val id: String,
        val navn: String,
    )
}