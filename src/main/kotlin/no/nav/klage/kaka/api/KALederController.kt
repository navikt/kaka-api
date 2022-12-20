package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.Saksbehandler
import no.nav.klage.kaka.api.view.TotalResponseV1
import no.nav.klage.kaka.api.view.TotalResponseV2
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.domain.kodeverk.Role.ROLE_KAKA_LEDERSTATISTIKK
import no.nav.klage.kaka.domain.kodeverk.Role.ROLE_KLAGE_LEDER
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportServiceV1
import no.nav.klage.kaka.services.ExportServiceV2
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
@Tag(name = "kaka-api:kaka-leder")
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
class KALederController(
    private val exportServiceV1: ExportServiceV1,
    private val exportServiceV2: ExportServiceV2,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper,
    private val tokenUtil: TokenUtil
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/export/excel", "/export/v{version}/excel")
    fun getAsExcel(
        @RequestParam(required = false) year: Int?,
        @PathVariable("version", required = false) version: Int?,
    ): ResponseEntity<ByteArray> {
        logger.debug("getAsExcel() called. Year param = $year, version = $version")

        validateIsKALeder()

        val yearToUse = if (year != null) Year.of(year) else Year.now()
        val fileAsBytes = if (version == 2) {
            exportServiceV2.getAsExcel(yearToUse)
        } else {
            exportServiceV1.getAsExcel(yearToUse)
        }

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
    ): TotalResponseV1 {
        logger.debug(
            "getTotalForLeder() called. enhetsnummer param = $enhetsnummer, " +
                    "fromMonth = $fromMonth, toMonth = $toMonth, saksbehandlere = $saksbehandlere"
        )

        validateIsKakaLeder()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet
        if (enhet.navn != enhetsnummer) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not leader of enhet $enhetsnummer")
        }

        return TotalResponseV1(
            anonymizedFinishedVurderingList = exportServiceV1.getFinishedForLederAsRawData(
                enhet = enhet,
                fromMonth = YearMonth.parse(fromMonth),
                toMonth = YearMonth.parse(toMonth),
                saksbehandlerIdentList = saksbehandlere,
            ),
            anonymizedUnfinishedVurderingList = exportServiceV1.getUnfinishedForLederAsRawData(
                enhet = enhet,
                toMonth = YearMonth.parse(toMonth),
                saksbehandlerIdentList = saksbehandlere,
            )
        )
    }

    @GetMapping("/statistics/v1/enheter/{enhetsnummer}/manager")
    fun getTotalForLederV1(
        @PathVariable enhetsnummer: String,
        @RequestParam(required = false) fromMonth: String?,
        @RequestParam(required = false) toMonth: String?,
        @RequestParam(required = false) saksbehandlere: List<String>?,
    ): TotalResponseV1 {
        return getTotalForLeder(enhetsnummer, fromMonth, toMonth, saksbehandlere)
    }

    @GetMapping("/statistics/v2/enheter/{enhetsnummer}/manager")
    fun getTotalForLederV2(
        @PathVariable enhetsnummer: String,
        @RequestParam(required = false) fromMonth: String?,
        @RequestParam(required = false) toMonth: String?,
        @RequestParam(required = false) saksbehandlere: List<String>?,
    ): TotalResponseV2 {
        logger.debug(
            "getTotalForLederV2() called. enhetsnummer param = $enhetsnummer, " +
                    "fromMonth = $fromMonth, toMonth = $toMonth, saksbehandlere = $saksbehandlere"
        )

        validateIsKakaLeder()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet
        if (enhet.navn != enhetsnummer) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not leader of enhet $enhetsnummer")
        }

        return TotalResponseV2(
            anonymizedFinishedVurderingList = exportServiceV2.getFinishedForLederAsRawData(
                enhet = enhet,
                fromMonth = YearMonth.parse(fromMonth),
                toMonth = YearMonth.parse(toMonth),
                saksbehandlerIdentList = saksbehandlere,
            ),
            anonymizedUnfinishedVurderingList = exportServiceV2.getUnfinishedForLederAsRawData(
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