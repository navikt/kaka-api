package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.AnonymizedFinishedVurdering
import no.nav.klage.kaka.api.view.AnonymizedUnfinishedVurdering
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.Year

@RestController
@Api(tags = ["kaka-api:kaka-export"])
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
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

    @GetMapping("/export/excel")
    fun getAsExcel(@RequestParam(required = false) year: Int?): ResponseEntity<ByteArray> {
        logger.debug("getAsExcel() called. Year param = $year")

        val roles = azureGateway.getRollerForInnloggetSaksbehandler().mapNotNull { rolleMapper.rolleMap[it.id] }

        val usersKlageenheter = axsysGateway.getKlageenheterForSaksbehandler(tokenUtil.getIdent())

        val fileAsBytes =
            exportService.getAsExcel(
                usersKlageenheter = usersKlageenheter,
                roles = roles,
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


    //TODO: Delete when not in use anymore
    @GetMapping("/export/raw")
    fun getAsRaw(
        @RequestParam(name = "year", required = false) inputYear: Int?,
        @RequestParam(required = false) fromDate: LocalDate?,
        @RequestParam(required = false) toDate: LocalDate?,
    ): TotalResponse {
        logger.debug("getAsRaw() called. Year param = $inputYear, fromDate = $fromDate, toDate = $toDate")

        return getTotalResponse(fromDate, toDate, inputYear)
    }

    @GetMapping("/statistics/total")
    fun getTotal(
        @RequestParam(name = "year", required = false) inputYear: Int?,
        @RequestParam(required = false) fromDate: LocalDate?,
        @RequestParam(required = false) toDate: LocalDate?,
    ): TotalResponse {
        logger.debug("getTotal() called. Year param = $inputYear, fromDate = $fromDate, toDate = $toDate")

        return getTotalResponse(fromDate, toDate, inputYear)
    }

    private fun getTotalResponse(
        fromDate: LocalDate?,
        toDate: LocalDate?,
        inputYear: Int?
    ): TotalResponse {
        if (fromDate != null && toDate != null) {
            return TotalResponse(
                anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByDates(
                    fromDate = fromDate,
                    toDate = toDate
                ),
                anonymizedUnfinishedVurderingList = exportService.getUnfinishedAsRawDataByToDate(
                    toDate = toDate
                )
            )
        } else {
            val year = if (inputYear != null) Year.of(inputYear) else Year.now()
            return TotalResponse(
                anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByYear(
                    year = year
                ),
                anonymizedUnfinishedVurderingList = exportService.getUnfinishedAsRawDataByYear(
                    year = year
                )
            )
        }
    }

    data class TotalResponse(
        val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurdering>,
        val anonymizedUnfinishedVurderingList: List<AnonymizedUnfinishedVurdering>,
    )
}