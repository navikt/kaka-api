package no.nav.klage.kaka.api.view

data class UserData(
    val ident: String,
    val navn: Navn,
    val ansattEnhet: EnhetKodeDto,
    val roller: List<String>,
    val expiresIn: Long,
) {
    data class Navn(
        val fornavn: String? = null,
        val etternavn: String? = null,
        val sammensattNavn: String?,
    )
}

data class EnhetKodeDto(val id: String, val navn: String)