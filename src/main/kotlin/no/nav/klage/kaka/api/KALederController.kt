package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.Saksbehandler
import no.nav.klage.kaka.api.view.TotalResponse
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.domain.kodeverk.Role.ROLE_KAKA_LEDERSTATISTIKK
import no.nav.klage.kaka.domain.kodeverk.Role.ROLE_KLAGE_LEDER
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Year
import java.time.YearMonth

@RestController
@Api(tags = ["kaka-api:kaka-leder"])
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
class KALederController(
    private val exportService: ExportService,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper,
    private val tokenUtil: TokenUtil
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/export/excel")
    fun getAsExcel(@RequestParam(required = false) year: Int?): ResponseEntity<ByteArray> {
        logger.debug("getAsExcel() called. Year param = $year")

        validateIsKALeder()

        val fileAsBytes =
            exportService.getAsExcel(
                year = if (year != null) Year.of(year) else Year.now()
            )

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

    @GetMapping("/statistics/enheter/{enhetsnummer}/manager")
    fun getTotalForLeder(
        @PathVariable enhetsnummer: String,
        @RequestParam(required = false) fromMonth: String?,
        @RequestParam(required = false) toMonth: String?,
        @RequestParam(required = false) saksbehandlere: List<String>?,
    ): TotalResponse {
        logger.debug(
            "getTotalForLeder() called. enhetsnummer param = $enhetsnummer, " +
                    "fromMonth = $fromMonth, toMonth = $toMonth, saksbehandlere = $saksbehandlere"
        )

        validateIsKakaLeder()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet
        if (enhet.navn != enhetsnummer) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not leader of enhet $enhetsnummer")
        }

        return TotalResponse(
            anonymizedFinishedVurderingList = exportService.getFinishedForLederAsRawData(
                enhet = enhet,
                fromMonth = YearMonth.parse(fromMonth),
                toMonth = YearMonth.parse(toMonth),
                saksbehandlerIdentList = saksbehandlere,
            ),
            anonymizedUnfinishedVurderingList = exportService.getUnfinishedForLederAsRawData(
                enhet = enhet,
                toMonth = YearMonth.parse(toMonth),
                saksbehandlerIdentList = saksbehandlere,
            )
        )
    }

    @GetMapping("/enheter/{enhetsnummer}/saksbehandlere")
    fun getSaksbehandlereForEnhet(
        @PathVariable enhetsnummer: String,
    ): List<Saksbehandler> {
        logger.debug("getSaksbehandlereForEnhet() called. enhetsnummer param = $enhetsnummer")

        validateIsKakaLeder()

        val saksbehandlerIdentList = azureGateway.getAnsatteIEnhet(enhetsnummer)

        return saksbehandlerIdentList.map {
            Saksbehandler(
                navIdent = it.navIdent,
                navn = it.displayName
            )
        }
    }

    private fun validateIsKALeder() {
        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (ROLE_KLAGE_LEDER !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role $ROLE_KLAGE_LEDER")
        }
    }

    private fun validateIsKakaLeder() {
        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (ROLE_KAKA_LEDERSTATISTIKK !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role $ROLE_KAKA_LEDERSTATISTIKK")
        }
    }
}