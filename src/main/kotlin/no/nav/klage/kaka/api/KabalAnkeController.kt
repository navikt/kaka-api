package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.KabalAnkeSaksdataInput
import no.nav.klage.kaka.api.view.KabalView
import no.nav.klage.kaka.api.view.ValidationErrors
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingService
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kodeverk.Source
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
@RequestMapping("/kabalanke")
class KabalAnkeController(
    private val kvalitetsvurderingService: KvalitetsvurderingService,
    private val saksdataService: SaksdataService,
    private val tokenUtil: TokenUtil,
    @Value("\${kabalAnkeApiName}")
    private val kabalAnkeApiName: String,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/kvalitetsvurdering")
    fun createKvalitetsvurdering(): KabalView {
        val callingApplication = verifyAndGetCallingApplication()
        logger.debug("New kvalitetsvurdering is requested by $callingApplication")
        return KabalView(kvalitetsvurderingService.createKvalitetsvurdering().id)
    }

    @GetMapping("/kvalitetsvurdering/{id}/validationerrors")
    fun getValidationErrors(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestParam temaId: String?,
        @RequestParam ytelseId: String?,
        @RequestParam typeId: String?
    ): ValidationErrors {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        val kvalitetsvurdering =
            kvalitetsvurderingService.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
        val ytelseToUse = ytelseId?.let { Ytelse.of(it) } ?: Ytelse.OMS_OMP
        val typeToUse = typeId?.let { Type.of(it) } ?: Type.ANKE

        return ValidationErrors(kvalitetsvurdering.getInvalidProperties(ytelseToUse, typeToUse).map {
            ValidationErrors.InvalidProperty(
                field = it.field,
                reason = it.reason
            )
        })
    }

    @PostMapping("/saksdata")
    fun createAndFinalizeSaksdata(
        @RequestBody input: KabalAnkeSaksdataInput
    ): KabalView {
        val callingApplication = verifyAndGetCallingApplication()
        logger.debug("Fullfør kvalitetsvurdering is requested by $callingApplication")
        return KabalView(
            saksdataService.createAndFinalizeSaksdataForAnke(
                sakenGjelder = input.sakenGjelder,
                sakstype = Type.of(input.sakstype),
                ytelse = Ytelse.of(input.ytelseId),
                vedtakKlageinstans = input.vedtakKlageinstans,
                vedtaksinstansEnhet = input.vedtaksinstansEnhet,
                mottattVedtaksinstans = input.mottattVedtaksinstans,
                utfall = Utfall.of(input.utfall),
                hjemler = input.registreringshjemler?.map { Registreringshjemmel.of(it) } ?: emptyList(),
                kvalitetsvurderingId = input.kvalitetsvurderingId,
                avsluttetAvSaksbehandler = input.avsluttetAvSaksbehandler,
                utfoerendeSaksbehandler = input.utfoerendeSaksbehandler,
                tilknyttetEnhet = input.tilknyttetEnhet,
                source = Source.KABAL,
            ).id
        )
    }

    private fun verifyAndGetCallingApplication(): String {
        val callingApplication = tokenUtil.getCallingApplication()
        if (callingApplication !in listOf(kabalAnkeApiName)) {
            throw MissingTilgangException("Calling application not allowed: $callingApplication")
        }
        return callingApplication
    }
}