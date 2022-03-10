package no.nav.klage.kaka.api.view

data class UserData(
    val ident: String,
    val navn: Navn,
    val ansattEnhet: KodeDto,
    val roller: List<String>,
) {
    data class Navn(
        val fornavn: String? = null,
        val etternavn: String? = null,
        val sammensattNavn: String?,
    )
}

data class KodeDto(val id: String, val navn: String, val beskrivelse: String)