package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.SaksdataListView
import no.nav.klage.kaka.api.view.toSaksdataSearchHitView
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.isLederVedtaksinstans
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Api(tags = ["kaka-api:saksdata"])
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
class SaksdataListController(
    private val tokenUtil: TokenUtil,
    private val saksdataService: SaksdataService,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/saksdataliste")
    fun searchKA(
        @RequestParam saksbehandlerIdent: String,
        @RequestParam fullfoert: Boolean,
        @RequestParam(required = false) sidenDager: Int?,
    ): SaksdataListView {
        logger.debug("{} is requested by ident {}", ::searchKA.name, tokenUtil.getIdent())

        validateIsSameUser(saksbehandlerIdent)

        return SaksdataListView(
            searchHits = saksdataService.search(saksbehandlerIdent, fullfoert, sidenDager)
                .map { it.toSaksdataSearchHitView() }
        )
    }

    @GetMapping("/saksdatalistelederfoersteinstans")
    fun searchVedtaksinstansleder(
        @RequestParam navIdent: String,
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): SaksdataListView {
        logger.debug(
            "{} is requested by ident {}. fromDate = {}, toDate = {}",
            ::searchVedtaksinstansleder.name,
            tokenUtil.getIdent(),
            fromDate,
            toDate,
        )
        validateIsSameUser(navIdent)

        val roller = azureGateway.getRollerForInnloggetSaksbehandler().mapNotNull { rolleMapper.rolleMap[it.id] }
        if (!isLederVedtaksinstans(roller)) {
            throw MissingTilgangException("user $navIdent is not leder vedtaksinstans")
        }

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        return SaksdataListView(
            searchHits = saksdataService.searchAsFoersteinstansleder(
                navIdent,
                enhet,
                fromDate,
                toDate
            ).map { it.toSaksdataSearchHitView() }
        )
    }

    private fun validateIsSameUser(saksbehandlerIdent: String) {
        if (saksbehandlerIdent != tokenUtil.getIdent()) {
            throw MissingTilgangException("User is not the same")
        }
    }

}