package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.KvalitetsvurderingV3Input
import no.nav.klage.kaka.api.view.KvalitetsvurderingV3View
import no.nav.klage.kaka.api.view.toKvalitetsvurderingV3View
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.services.KvalitetsvurderingV3Service
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logKvalitetsvurderingMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.JsonNode
import java.util.*

@RestController
@Tag(name = "kaka-api:kvalitetsvurdering-v3")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping(value = ["/kvalitetsvurderinger/v3/{id}", "/kvalitetsvurderinger/v3/{id}/"])
class KvalitetsvurderingV3Controller(
    private val kvalitetsvurderingV3Service: KvalitetsvurderingV3Service,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PatchMapping
    fun patchKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @Schema(implementation = KvalitetsvurderingV3Input::class)
        @RequestBody
        data: JsonNode
    ): KvalitetsvurderingV3View {
        return kvalitetsvurderingV3Service.patchKvalitetsvurdering(kvalitetsvurderingId, data)
            .toKvalitetsvurderingV3View()
    }

    @GetMapping
    fun getKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID
    ): KvalitetsvurderingV3View {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logKvalitetsvurderingMethodDetails(
            ::getKvalitetsvurdering.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )
        return kvalitetsvurderingV3Service.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
            .toKvalitetsvurderingV3View()
    }
}