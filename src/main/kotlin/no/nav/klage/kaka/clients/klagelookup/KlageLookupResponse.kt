package no.nav.klage.kaka.clients.klagelookup

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


data class UsersResponse(
    val users: List<UserResponse>,
)

data class UserResponse (
    val navIdent: String,
    val sammensattNavn: String,
    val fornavn: String,
    val etternavn: String,
)

data class Access(
    val access: Boolean,
    val reason: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExtendedUserResponse (
    val navIdent: String,
    val sammensattNavn: String,
    val fornavn: String,
    val etternavn: String,
    val enhet: Enhet,
)

data class Enhet (
    val enhetNr: String,
    val enhetNavn: String,
)