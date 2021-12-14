package no.nav.klage.kaka.api.view

data class UserData(val ident: String, val navn: Navn) {
    data class Navn(
        val fornavn: String? = null,
        val etternavn: String? = null,
        val sammensattNavn: String?,
    )
}