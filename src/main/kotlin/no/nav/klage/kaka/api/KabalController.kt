package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.KabalView
import no.nav.klage.kaka.api.view.SaksdataInput
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.kodeverk.Hjemmel
import no.nav.klage.kaka.domain.kodeverk.Sakstype
import no.nav.klage.kaka.domain.kodeverk.Tema
import no.nav.klage.kaka.domain.kodeverk.Utfall
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingService
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["kaka-api:kabal-kvalitet"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/kabal")
class KabalKvalitetsvurderingController(
    private val kvalitetsvurderingService: KvalitetsvurderingService,
    private val saksdataService: SaksdataService,
    private val tokenUtil: TokenUtil,
    @Value("\${kabalApiName}")
    private val kabalApiName: String
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/kvalitetsvurdering")
    fun createKvalitetsvurdering(): KabalView {
        val callingApplication = tokenUtil.getCallingApplication()
        if (callingApplication != kabalApiName) {
            throw MissingTilgangException("Wrong calling application: $callingApplication")
        }
        logger.debug("New kvalitetsvurdering is requested by kabal-api")
        return KabalView(kvalitetsvurderingService.createKvalitetsvurdering().id)
    }

    @PostMapping("/saksdata")
    fun createAndFinalizeSaksdata(
        @RequestBody input: SaksdataInput
    ): KabalView {
        val callingApplication = tokenUtil.getCallingApplication()
        if (callingApplication != kabalApiName) {
            throw MissingTilgangException("Wrong calling application: $callingApplication")
        }
        logger.debug("Fullfør kvalitetsvurdering is requested by kabal-api")
        return KabalView(
            saksdataService.createAndFinalizeSaksdata(
                sakenGjelder = input.sakenGjelder,
                sakstype = Sakstype.of(input.sakstype),
                tema = Tema.of(input.tema),
                mottattKlageinstans = input.mottattKlageinstans,
                vedtaksinstansEnhet = input.vedtaksinstansEnhet,
                mottattVedtaksinstans = input.mottattVedtaksinstans,
                utfall = Utfall.of(input.utfall),
                hjemler = input.hjemler.map { Hjemmel.of(it) },
                kvalitetsvurderingId = input.kvalitetsvurderingId,
                avsluttetAvSaksbehandler = input.avsluttetAvSaksbehandler,
                utfoerendeSaksbehandler = input.utfoerendeSaksbehandler,
            ).id
        )
    }
}