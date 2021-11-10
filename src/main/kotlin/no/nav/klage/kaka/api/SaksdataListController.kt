package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.SaksdataListView
import no.nav.klage.kaka.api.view.toSaksdataSearchHitView
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["kaka-api:saksdata"])
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
@RequestMapping("/saksdataliste")
class SaksdataListController(
    private val tokenUtil: TokenUtil,
    private val saksdataService: SaksdataService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/")
    fun search(
        @RequestParam saksbehandlerIdent: String,
        @RequestParam fullfoert: Boolean,
        @RequestParam(required = false) sidenDager: Int?,
        ): SaksdataListView {
        logger.debug("{} is requested by ident {}", ::search.name, tokenUtil.getIdent())

        validateIsSameUser(saksbehandlerIdent)

        return SaksdataListView(
            searchHits = saksdataService.search(saksbehandlerIdent, fullfoert, sidenDager)
                .map { it.toSaksdataSearchHitView() }
        )
    }

    private fun validateIsSameUser(saksbehandlerIdent: String) {
        if (saksbehandlerIdent != tokenUtil.getIdent()) {
            throw MissingTilgangException("Asking for data from another saksbehandler is not allowed")
        }
    }

}