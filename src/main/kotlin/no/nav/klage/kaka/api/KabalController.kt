package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.KabalSaksdataInput
import no.nav.klage.kaka.api.view.KabalView
import no.nav.klage.kaka.api.view.ValidationErrors
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingService
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.util.*

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
        private const val KA_NORD = "4295"
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

    @GetMapping("/kvalitetsvurdering/{id}/validationerrors")
    fun getValidationErrors(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestParam temaId: String,
        @RequestParam ytelseId: String?,
    ): ValidationErrors {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        val kvalitetsvurdering =
            kvalitetsvurderingService.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
        val ytelseToUse = if (ytelseId != null) Ytelse.of(ytelseId) else Ytelse.OMS_OMP
        //FIXME use type from input
        return ValidationErrors(kvalitetsvurdering.getInvalidProperties(ytelseToUse, Type.KLAGE).map {
            ValidationErrors.InvalidProperty(
                field = it.field,
                reason = it.reason
            )
        })
    }

    @PostMapping("/saksdata")
    fun createAndFinalizeSaksdata(
        @RequestBody input: KabalSaksdataInput
    ): KabalView {
        val callingApplication = tokenUtil.getCallingApplication()
        if (callingApplication != kabalApiName) {
            throw MissingTilgangException("Wrong calling application: $callingApplication")
        }
        logger.debug("Fullfør kvalitetsvurdering is requested by kabal-api")
        return KabalView(
            saksdataService.createAndFinalizeSaksdata(
                sakenGjelder = input.sakenGjelder,
                sakstype = Type.of(input.sakstype),
                ytelse = Ytelse.of(input.ytelseId),
                mottattKlageinstans = input.mottattKlageinstans,
                vedtaksinstansEnhet = input.vedtaksinstansEnhet,
                mottattVedtaksinstans = input.mottattVedtaksinstans,
                utfall = Utfall.of(input.utfall),
                hjemler = input.registreringshjemler?.map { Registreringshjemmel.of(it) } ?: emptyList(),
                kvalitetsvurderingId = input.kvalitetsvurderingId,
                avsluttetAvSaksbehandler = input.avsluttetAvSaksbehandler,
                utfoerendeSaksbehandler = input.utfoerendeSaksbehandler,
                tilknyttetEnhet = input.tilknyttetEnhet,
            ).id
        )
    }
}