package no.nav.klage.kaka.api

import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.AdminService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/admin")
class AdminController(
    private val tokenUtil: TokenUtil,
    private val rolleMapper: RolleMapper,
    private val adminService: AdminService,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/loginvalidsakengjelder")
    fun logCorruptData() {
        krevAdminTilgang()
        adminService.logInvalidSakenGjelder()
    }

    @GetMapping("/logv1hjemlerinv2")
    fun logV1HjemlerInV2() {
        krevAdminTilgang()
        adminService.logV1HjemlerInV2()
    }

    @GetMapping("/migrateTilbakekreving")
    fun migrateTilbakekreving() {
        krevAdminTilgang()
        adminService.migrateTilbakekreving()
    }

    private fun krevAdminTilgang() {
        val roller = rolleMapper.toRoles(tokenUtil.getGroups())
        if (!roller.contains(Role.KAKA_ADMIN)) {
            throw MissingTilgangException("Not an admin")
        }
    }
}