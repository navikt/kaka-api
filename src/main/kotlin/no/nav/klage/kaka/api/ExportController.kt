package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.RolleMapper
import no.nav.klage.kaka.clients.axsys.AxsysGateway
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.services.ExportService
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["kaka-api:kaka-export"])
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
@RequestMapping("/export")
class ExportController(
    private val exportService: ExportService,
    private val axsysGateway: AxsysGateway,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper,
    private val tokenUtil: TokenUtil
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/excel")
    fun getAsExcel(): ResponseEntity<ByteArray> {
        logger.debug("getAsExcel() called")

        val roles = azureGateway.getRollerForInnloggetSaksbehandler().mapNotNull { rolleMapper.rolleMap[it.id] }

        val usersKlageenheter = axsysGateway.getKlageenheterForSaksbehandler(tokenUtil.getIdent())

        val fileAsBytes = exportService.getAsExcel(usersKlageenheter = usersKlageenheter, roles = roles)

        val responseHeaders = HttpHeaders()
        responseHeaders.contentType =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        responseHeaders.add("Content-Disposition", "inline; filename=export.xlsx")
        return ResponseEntity(
            fileAsBytes,
            responseHeaders,
            HttpStatus.OK
        )
    }

}