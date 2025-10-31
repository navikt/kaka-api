package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.CensoredKvalitetsvurderingV3View
import no.nav.klage.kaka.api.view.toCensoredKvalitetsvurderingV3View
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.KvalitetsvurderingV3Service
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logKvalitetsvurderingMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Tag(name = "kaka-api:kvalitetsvurdering-v3")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/censoredkvalitetsvurderinger/v3/{id}")
class CensoredKvalitetsvurderingV3Controller(
    private val kvalitetsvurderingV3Service: KvalitetsvurderingV3Service,
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
    ): CensoredKvalitetsvurderingV3View {
        validateCanSeeKvalitetstilbakemeldinger()

        val innloggetSaksbehandler = tokenUtil.getIdent()
        logKvalitetsvurderingMethodDetails(
            ::getCensoredKvalitetsvurdering.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )
        return kvalitetsvurderingV3Service.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
            .toCensoredKvalitetsvurderingV3View()
    }

    private fun validateCanSeeKvalitetstilbakemeldinger() {
        val roles = roleMapper.toRoles(tokenUtil.getGroups())
        if (Role.KAKA_KVALITETSTILBAKEMELDINGER !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role ${Role.KAKA_KVALITETSTILBAKEMELDINGER}")
        }
    }
}