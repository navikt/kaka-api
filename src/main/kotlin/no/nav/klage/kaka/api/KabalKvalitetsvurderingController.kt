package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.KabalKvalitetsvurderingView
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["kaka-api:kabal-kvalitet"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/kvalitetsvurdering")
class KabalKvalitetsvurderingController(
    private val kvalitetsvurderingService: KvalitetsvurderingService,
    private val tokenUtil: TokenUtil,
    @Value("\${kabalApiName}")
    private val kabalApiName: String
) {

    @PostMapping
    fun createKvalitetsvurdering(): KabalKvalitetsvurderingView {
        val callingApplication = tokenUtil.getCallingApplication()
        if (callingApplication != kabalApiName) {
            throw MissingTilgangException("Wrong calling application: $callingApplication")
        }
        return KabalKvalitetsvurderingView(kvalitetsvurderingService.createKvalitetsvurdering().id)
    }
}