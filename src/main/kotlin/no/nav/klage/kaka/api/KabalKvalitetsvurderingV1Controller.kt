package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.KabalSaksdataInput
import no.nav.klage.kaka.api.view.KabalView
import no.nav.klage.kaka.api.view.KabalViewIdOnly
import no.nav.klage.kaka.api.view.ValidationErrors
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingV1Service
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
@Tag(name = "kaka-api:kabal-kvalitet-v1")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/kabal")
class KabalKvalitetsvurderingV1Controller(
    private val kvalitetsvurderingV1Service: KvalitetsvurderingV1Service,
    private val saksdataService: SaksdataService,
    private val tokenUtil: TokenUtil,
    @Value("\${kabalApiName}")
    private val kabalApiName: String,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/kvalitetsvurdering", "/kvalitetsvurderinger/v1")
    fun createKvalitetsvurdering(): KabalView {
        val callingApplication = verifyAndGetCallingApplication()
        logger.debug("New kvalitetsvurdering is requested by $callingApplication")

        return with(kvalitetsvurderingV1Service.createKvalitetsvurdering()) {
            KabalView(
                id = this.id,
                kvalitetsvurderingId = this.id,
                kvalitetsvurderingVersion = 1,
            )
        }
    }

    @DeleteMapping("/kvalitetsvurdering","/kvalitetsvurderinger/v1/{id}")
    fun deleteKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID,
    ) {
        val callingApplication = verifyAndGetCallingApplication()
        logger.debug("Delete kvalitetsvurdering is requested by $callingApplication")
        kvalitetsvurderingV1Service.deleteKvalitetsvurdering(kvalitetsvurderingId)
        logger.debug("Successfully deleted kvalitetsvurdering $kvalitetsvurderingId")
    }

    @GetMapping("/kvalitetsvurdering/{id}/validationerrors", "/kvalitetsvurderinger/v1/{id}/validationerrors")
    fun getValidationErrors(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestParam temaId: String?,
        @RequestParam ytelseId: String?,
        @RequestParam typeId: String?
    ): ValidationErrors {
        val innloggetSaksbehandler = tokenUtil.getIdent()

        val kvalitetsvurdering =
            kvalitetsvurderingV1Service.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
        val ytelseToUse = ytelseId?.let { Ytelse.of(it) } ?: Ytelse.OMS_OMP
        val typeToUse = typeId?.let { Type.of(it) } ?: Type.KLAGE

        return ValidationErrors(kvalitetsvurdering.getInvalidProperties(ytelseToUse, typeToUse).map {
            ValidationErrors.InvalidProperty(
                field = it.field,
                reason = it.reason
            )
        })
    }

    @PostMapping("/saksdata", "/saksdata/v1")
    fun createAndFinalizeSaksdata(
        @RequestBody input: KabalSaksdataInput
    ): KabalViewIdOnly {
        val callingApplication = verifyAndGetCallingApplication()
        logger.debug("Fullfør kvalitetsvurdering is requested by $callingApplication")
        return KabalViewIdOnly(
            saksdataService.handleIncomingCompleteSaksdata(
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
                source = Source.KABAL,
                kvalitsvurderingVersion = 1,
            ).id
        )
    }

    private fun verifyAndGetCallingApplication(): String {
        val callingApplication = tokenUtil.getCallingApplication()
        if (callingApplication !in listOf(kabalApiName)) {
            throw MissingTilgangException("Calling application not allowed: $callingApplication")
        }
        return callingApplication
    }
}