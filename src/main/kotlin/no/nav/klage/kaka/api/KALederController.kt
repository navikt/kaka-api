package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.ExcelQueryParams
import no.nav.klage.kaka.api.view.ManagerResponseV1
import no.nav.klage.kaka.api.view.ManagerResponseV2
import no.nav.klage.kaka.api.view.Saksbehandler
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.domain.kodeverk.Role.*
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportServiceV1
import no.nav.klage.kaka.services.ExportServiceV2
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream
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

    @GetMapping("/export/v{version}/excel", "/export/v{version}/excel-med-fritekst")
    fun getAsExcelMedFritekst(
        queryParams: ExcelQueryParams,
    ): ResponseEntity<Resource> {
        logger.debug("getAsExcelMedFritekst() called. Query params = {}", queryParams)

        validateHasExcelMedFritekst()

        val file = if (queryParams.version == 2) {
            exportServiceV2.getAsExcel(includeFritekst = true, queryParams = queryParams)
        } else {
//            exportServiceV1.getAsExcel(year = yearToUse, includeFritekst = true)
            TODO()
        }

        val responseHeaders = HttpHeaders()
        responseHeaders.add("Content-Disposition", "inline; filename=export.xlsx")

        return try {
            ResponseEntity.ok()
                .headers(responseHeaders)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(InputStreamResource(FileInputStream(file)))
        } finally {
            try {
                file.delete()
            } catch (e: Exception) {
                logger.warn("could not delete temporary excel file", e)
            }
        }
    }

    @GetMapping("/export/v{version}/excel-uten-fritekst")
    fun getAsExcelUtenFritekst(
        queryParams: ExcelQueryParams,
    ): ResponseEntity<Resource> {
        logger.debug("getAsExcelUtenFritekst() called. Query params = {}", queryParams)

        validateHasExcelUtenFritekst()

        val file = if (queryParams.version == 2) {
            exportServiceV2.getAsExcel(includeFritekst = false, queryParams = queryParams)
        } else {
//            exportServiceV1.getAsExcel(year = yearToUse, includeFritekst = false)
            TODO()
        }

        val responseHeaders = HttpHeaders()
        responseHeaders.add("Content-Disposition", "inline; filename=export.xlsx")

        return try {
            ResponseEntity.ok()
                .headers(responseHeaders)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(InputStreamResource(FileInputStream(file)))
        } finally {
            try {
                file.delete()
            } catch (e: Exception) {
                logger.warn("could not delete temporary excel file", e)
            }
        }
    }

    @GetMapping("/statistics/enheter/{enhetsnummer}/manager")
    fun getTotalForLeder(
        @PathVariable enhetsnummer: String,
        @RequestParam(required = false) fromMonth: String?,
        @RequestParam(required = false) toMonth: String?,
        @RequestParam(required = false) saksbehandlere: List<String>?,
    ): ManagerResponseV1 {
        logger.debug(
            "getTotalForLeder() called. enhetsnummer param = $enhetsnummer, " +
                    "fromMonth = $fromMonth, toMonth = $toMonth, saksbehandlere = $saksbehandlere"
        )

        validateIsKakaLeder()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet
        if (enhet.navn != enhetsnummer) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not leader of enhet $enhetsnummer")
        }

        val data = exportServiceV1.getFinishedForLederAsRawData(
            enhet = enhet,
            fromMonth = YearMonth.parse(fromMonth),
            toMonth = YearMonth.parse(toMonth),
            saksbehandlerIdentList = saksbehandlere,
        )

        return ManagerResponseV1(
            anonymizedFinishedVurderingList = if (saksbehandlere?.isNotEmpty() == true) {
                data.saksbehandlere.values.flatten()
            } else {
                data.mine + data.saksbehandlere.values.flatten()
            },
            saksbehandlere = data.saksbehandlere,
            mine = data.mine,
            rest = data.rest,
        )
    }

    @GetMapping("/statistics/v1/enheter/{enhetsnummer}/manager")
    fun getTotalForLederV1(
        @PathVariable enhetsnummer: String,
        @RequestParam(required = false) fromMonth: String?,
        @RequestParam(required = false) toMonth: String?,
        @RequestParam(required = false) saksbehandlere: List<String>?,
    ): ManagerResponseV1 {
        return getTotalForLeder(enhetsnummer, fromMonth, toMonth, saksbehandlere)
    }

    @GetMapping("/statistics/v2/enheter/{enhetsnummer}/manager")
    fun getTotalForLederV2(
        @PathVariable enhetsnummer: String,
        @RequestParam(required = false) fromMonth: String?,
        @RequestParam(required = false) toMonth: String?,
        @RequestParam(required = false) saksbehandlere: List<String>?,
    ): ManagerResponseV2 {
        logger.debug(
            "getTotalForLederV2() called. enhetsnummer param = $enhetsnummer, " +
                    "fromMonth = $fromMonth, toMonth = $toMonth, saksbehandlere = $saksbehandlere"
        )

        validateIsKakaLeder()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet
        if (enhet.navn != enhetsnummer) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not leader of enhet $enhetsnummer")
        }

        val data = exportServiceV2.getFinishedForLederAsRawData(
            enhet = enhet,
            fromMonth = YearMonth.parse(fromMonth),
            toMonth = YearMonth.parse(toMonth),
            saksbehandlerIdentList = saksbehandlere,
        )
        return ManagerResponseV2(
            anonymizedFinishedVurderingList = if (saksbehandlere?.isNotEmpty() == true) {
                data.saksbehandlere.values.flatten()
            } else {
                data.mine + data.saksbehandlere.values.flatten()
            },
            saksbehandlere = data.saksbehandlere,
            mine = data.mine,
            rest = data.rest,
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

    private fun validateHasExcelMedFritekst() {
        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (KAKA_EXCEL_UTTREKK_MED_FRITEKST !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role $KAKA_EXCEL_UTTREKK_MED_FRITEKST")
        }
    }

    private fun validateHasExcelUtenFritekst() {
        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (KAKA_EXCEL_UTTREKK_UTEN_FRITEKST !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role $KAKA_EXCEL_UTTREKK_UTEN_FRITEKST")
        }
    }

    private fun validateIsKakaLeder() {
        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (KAKA_LEDERSTATISTIKK !in roles) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} does not have the role $KAKA_LEDERSTATISTIKK")
        }
    }
}