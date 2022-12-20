package no.nav.klage.kaka.api

import com.fasterxml.jackson.databind.node.*
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.*
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingV2Service
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logKvalitetsvurderingMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kaka-api:kvalitetsvurdering-v2")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/censoredkvalitetsvurderinger/v2/{id}")
class CensoredKvalitetsvurderingV2Controller(
    private val kvalitetsvurderingV2Service: KvalitetsvurderingV2Service,
    private val tokenUtil: TokenUtil,
    private val roleMapper: RolleMapper,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping
    fun getCensoredKvalitetsvurdering(
        @PathVariable("id") kvalitetsvurderingId: UUID
    ): CensoredKvalitetsvurderingV2View {
        validateCanSeeKvalitetstilbakemeldinger()

        val innloggetSaksbehandler = tokenUtil.getIdent()
        logKvalitetsvurderingMethodDetails(
            ::getCensoredKvalitetsvurdering.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )
        return kvalitetsvurderingV2Service.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
            .toCensoredKvalitetsvurderingV2View()
    }

    private fun validateCanSeeKvalitetstilbakemeldinger() {
        val roles = roleMapper.toRoles(tokenUtil.getGroups())
        if (Role.ROLE_KAKA_KVALITETSTILBAKEMELDINGER !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role ${Role.ROLE_KAKA_KVALITETSTILBAKEMELDINGER}")
        }
    }
}