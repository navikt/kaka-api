package no.nav.klage.kaka.clients.axsys

import no.nav.klage.kaka.clients.azure.AzureGateway
import org.springframework.stereotype.Service
import no.nav.klage.kodeverk.Enhet as KodeverkEnhet

@Service
class AxsysGateway(
    private val azureGateway: AzureGateway
) {

    fun getKlageenheterForSaksbehandler(ident: String): List<KodeverkEnhet> =
        azureGateway.getKlageenheterForSaksbehandler(ident)
}