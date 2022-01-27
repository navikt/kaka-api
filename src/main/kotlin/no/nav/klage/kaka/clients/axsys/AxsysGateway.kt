package no.nav.klage.kaka.clients.axsys

import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerIdent
import org.springframework.stereotype.Service
import no.nav.klage.kodeverk.Enhet as KodeverkEnhet

@Service
class AxsysGateway(
    private val azureGateway: AzureGateway,
    private val axsysClient: AxsysClient,
) {

    fun getKlageenheterForSaksbehandler(ident: String): List<KodeverkEnhet> =
        azureGateway.getKlageenheterForSaksbehandler(ident)

    @Deprecated("Må erstattes med å hente data fra Azure")
    fun getSaksbehandlereIEnhet(enhetId: String): List<SaksbehandlerIdent> {
        return axsysClient.getSaksbehandlereIEnhet(enhetId).map { SaksbehandlerIdent(it.appIdent) }
    }
}