package no.nav.klage.kaka.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.KvalitetsvurderingV2Input
import no.nav.klage.kaka.api.view.KvalitetsvurderingV2View
import no.nav.klage.kaka.api.view.toKvalitetsvurderingV2View
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.services.KvalitetsvurderingV2Service
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logKvalitetsvurderingMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kaka-api:kvalitetsvurdering-v2")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping(value = ["/kvalitetsvurderinger/v2/{id}", "/kvalitetsvurderinger/v2/{id}/"])
class KvalitetsvurderingV2Controller(
    private val kvalitetsvurderingV2Service: KvalitetsvurderingV2Service,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PatchMapping
    fun patchKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @Schema(implementation = KvalitetsvurderingV2Input::class)
        @RequestBody
        data: JsonNode
    ): KvalitetsvurderingV2View {
        return kvalitetsvurderingV2Service.patchKvalitetsvurdering(kvalitetsvurderingId, data)
            .toKvalitetsvurderingV2View()
    }

    @GetMapping
    fun getKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID
    ): KvalitetsvurderingV2View {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logKvalitetsvurderingMethodDetails(
            ::getKvalitetsvurdering.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )
        return kvalitetsvurderingV2Service.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
            .toKvalitetsvurderingV2View()
    }
}