package no.nav.klage.kaka.api


import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.UserData
import no.nav.klage.kaka.services.SaksbehandlerService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "kaka-api:metadata")
@Unprotected
@RequestMapping("/metadata")
class MetadataController(
    private val tokenUtil: TokenUtil,
    private val saksbehandlerService: SaksbehandlerService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/userdata", produces = ["application/json"])
    fun getUserData(): UserData {
        val navIdent = tokenUtil.getIdent()
        return saksbehandlerService.getUserData(navIdent = navIdent)
    }
}