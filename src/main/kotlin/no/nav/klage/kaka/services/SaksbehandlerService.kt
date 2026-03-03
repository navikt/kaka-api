package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.EnhetKodeDto
import no.nav.klage.kaka.api.view.UserData
import no.nav.klage.kaka.clients.klagelookup.ExtendedUserResponse
import no.nav.klage.kaka.clients.klagelookup.KlageLookupClient
import no.nav.klage.kaka.exceptions.EnhetNotFoundForSaksbehandlerException
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kodeverk.Enhet
import org.springframework.stereotype.Service

@Service
class SaksbehandlerService(
    private val klageLookupClient: KlageLookupClient,
    private val rolleMapper: RolleMapper,
    private val tokenUtil: TokenUtil,
) {

    fun getUserInfo(navIdent: String): ExtendedUserResponse {
        val userInfo = klageLookupClient.getUserInfo(navIdent = navIdent)
        return userInfo
    }

    fun getUserData(navIdent: String): UserData {
        val roller = rolleMapper.toRoles(tokenUtil.getGroups())
        val userInfo = getUserInfo(navIdent = navIdent)
        return UserData(
            ident = navIdent,
            navn = userInfo.toNavnView(),
            ansattEnhet = userInfo.toEnhetKodeDto(),
            roller = roller.map { it.name }
        )
    }

    fun getUserEnhet(navIdent: String): Enhet {
        return getUserInfo(navIdent = navIdent).toEnhet()
    }

    private fun ExtendedUserResponse.toEnhetKodeDto(): EnhetKodeDto {
        return EnhetKodeDto(
            id = enhet.enhetNr,
            navn = enhet.enhetNavn,
        )
    }

    private fun ExtendedUserResponse.toEnhet(): Enhet {
        return Enhet.entries.find { it.navn == enhet.enhetNr }
            ?: throw EnhetNotFoundForSaksbehandlerException("Enhet ikke funnet med enhetNr ${enhet.enhetNr}")
    }

    private fun ExtendedUserResponse.toNavnView(): UserData.Navn {
        return UserData.Navn(
            fornavn = fornavn, etternavn = etternavn, sammensattNavn = sammensattNavn
        )
    }
}
