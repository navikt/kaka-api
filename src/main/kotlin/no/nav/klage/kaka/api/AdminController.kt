package no.nav.klage.kaka.api

import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.clients.ereg.Organisasjon
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.AdminService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/admin")
class AdminController(
    private val azureGateway: AzureGateway,
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

    private fun krevAdminTilgang() {
        val roller = rolleMapper.toRoles(azureGateway.getRollerForInnloggetSaksbehandler())
        if (!roller.contains(Role.ROLE_ADMIN)) {
            throw MissingTilgangException("Not an admin")
        }
    }

    //Temporary for debugging in prod
    @GetMapping("/organisasjon/{orgnr}")
    fun getOrganisasjon(
        @PathVariable("orgnr") orgnr: String
    ): Organisasjon? {
        krevAdminTilgang()
        return adminService.getOrganisasjon(orgnr)
    }
}