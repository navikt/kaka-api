package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.KvalitetsvurderingV1Input
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.services.KvalitetsvurderingV1Service
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logKvalitetsvurderingMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.JsonNode
import java.util.*

@RestController
@Tag(name = "kaka-api:kvalitetsvurdering-v1")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/kvalitetsvurderinger/v1/{id}", "/kvalitetsvurdering/{id}")
class KvalitetsvurderingV1Controller(
    private val kvalitetsvurderingV1Service: KvalitetsvurderingV1Service,
    private val tokenUtil: TokenUtil
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PatchMapping
    fun patchKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @Schema(implementation = KvalitetsvurderingV1Input::class)
        @RequestBody
        data: JsonNode
    ): KvalitetsvurderingV1 {
        return kvalitetsvurderingV1Service.patchKvalitetsvurdering(kvalitetsvurderingId, data)
    }

    @GetMapping
    fun getKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID
    ): KvalitetsvurderingV1 {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logKvalitetsvurderingMethodDetails(
            ::getKvalitetsvurdering.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )
        return kvalitetsvurderingV1Service.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
    }
}