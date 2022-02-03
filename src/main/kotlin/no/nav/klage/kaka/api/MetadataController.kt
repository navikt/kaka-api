package no.nav.klage.kaka.api


import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.KodeDto
import no.nav.klage.kaka.api.view.RolleMapper
import no.nav.klage.kaka.api.view.UserData
import no.nav.klage.kaka.api.view.isLederVedtaksinstans
import no.nav.klage.kaka.clients.axsys.AxsysGateway
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerPersonligInfo
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["kaka-api:metadata"])
@Unprotected
@RequestMapping("/metadata")
class MetadataController(
    private val tokenUtil: TokenUtil,
    private val axsysGateway: AxsysGateway,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/userdata", produces = ["application/json"])
    fun getUserData(): UserData {
        val roller = azureGateway.getRollerForInnloggetSaksbehandler().mapNotNull { rolleMapper.rolleMap[it.id] }

        val usersKlageenheter = if (isLederVedtaksinstans(roller)) {
            emptyList()
        } else {
            axsysGateway.getKlageenheterForSaksbehandler(tokenUtil.getIdent())
        }

        return UserData(
            ident = tokenUtil.getIdent(),
            navn = azureGateway.getDataOmInnloggetSaksbehandler().toNavn(),
            klageenheter = usersKlageenheter.map { KodeDto(id = it.id, navn = it.navn, beskrivelse = it.beskrivelse) },
            ansattEnhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet.let {
                KodeDto(
                    id = it.id,
                    navn = it.navn,
                    beskrivelse = it.beskrivelse
                )
            },
            roller = roller
        )
    }

    private fun SaksbehandlerPersonligInfo.toNavn(): UserData.Navn =
        UserData.Navn(
            fornavn = this.fornavn,
            etternavn = this.etternavn,
            sammensattNavn = this.sammensattNavn,
        )
}