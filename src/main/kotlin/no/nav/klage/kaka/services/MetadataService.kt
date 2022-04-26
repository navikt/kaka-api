package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.EnhetKodeDto
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.clients.norg2.Norg2Client
import org.springframework.stereotype.Service

@Service
class MetadataService(
    private val norg2Client: Norg2Client,
    private val azureGateway: AzureGateway,
) {
    fun getInnloggetSaksbehandlerEnhetKodeDto(): EnhetKodeDto {
        val enhet = norg2Client.getEnhet(azureGateway.getStreetAddressInnloggetSaksbehandler())!!
        return EnhetKodeDto(
            id = enhet.enhetNr,
            navn = enhet.navn,
        )
    }
}
