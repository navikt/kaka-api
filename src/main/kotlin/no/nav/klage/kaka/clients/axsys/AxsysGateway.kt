package no.nav.klage.kaka.clients.axsys

import no.nav.klage.kodeverk.klageenheter
import org.springframework.stereotype.Service
import no.nav.klage.kodeverk.Enhet as KodeverkEnhet

@Service
class AxsysGateway(
    private val axsysClient: AxsysClient,
    private val tilgangerMapper: TilgangerMapper
) {

    fun getKlageenheterForSaksbehandler(ident: String): List<KodeverkEnhet> =
        tilgangerMapper.mapTilgangerToEnheter(axsysClient.getTilgangerForSaksbehandler(ident))
            .filter { it in klageenheter }
}