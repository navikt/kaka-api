package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.TotalResponse
import no.nav.klage.kaka.api.view.TotalResponseWithoutEnheter
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.isLederVedtaksinstans
import no.nav.security.token.support.core.api.ProtectedWithClaims
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
    private val tokenUtil: TokenUtil,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/statistics/my")
    fun getMyStats(
        @RequestParam(name = "year", required = false) inputYear: Int?,
        @RequestParam(required = false) fromDate: LocalDate?,
        @RequestParam(required = false) toDate: LocalDate?,
    ): TotalResponse {
        logger.debug("getMyStats() called. Year param = $inputYear, fromDate = $fromDate, toDate = $toDate")

        val innloggetSaksbehandler = tokenUtil.getIdent()

        if (fromDate != null && toDate != null) {
            return TotalResponse(
                anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByDatesAndSaksbehandler(
                    fromDate = fromDate,
                    toDate = toDate,
                    saksbehandler = innloggetSaksbehandler,
                ),
                anonymizedUnfinishedVurderingList = exportService.getUnfinishedAsRawDataByToDateAndSaksbehandler(
                    toDate = toDate,
                    saksbehandler = innloggetSaksbehandler,
                )
            )
        } else {
            val year = getYear(inputYear)
            return TotalResponse(
                anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByYearAndSaksbehandler(
                    year = year,
                    saksbehandler = innloggetSaksbehandler,
                ),
                anonymizedUnfinishedVurderingList = exportService.getUnfinishedAsRawDataByYearAndSaksbehandler(
                    year = year,
                    saksbehandler = innloggetSaksbehandler,
                )
            )
        }
    }

    @GetMapping("/statistics/total")
    fun getTotal(
        @RequestParam(name = "year", required = false) inputYear: Int?,
        @RequestParam(required = false) fromDate: LocalDate?,
        @RequestParam(required = false) toDate: LocalDate?,
    ): TotalResponse {
        logger.debug("getTotal() called. Year param = $inputYear, fromDate = $fromDate, toDate = $toDate")

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
            val year = getYear(inputYear)
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

    @GetMapping("/statistics/vedtaksinstansleder")
    fun getTotalForVedtaksinstansleder(
        @RequestParam(name = "year", required = false) inputYear: Int?,
        @RequestParam(required = false) fromDate: LocalDate?,
        @RequestParam(required = false) toDate: LocalDate?,
        @RequestParam(required = false) mangelfullt: List<String>?,
        @RequestParam(required = false) kommentarer: List<String>?,
    ): TotalResponseWithoutEnheter {
        logger.debug(
            "getTotalForVedtaksinstansleder() called. Year param = {}, fromDate = {}, toDate = {}, mangelfullt = {}, kommentarer = {}",
            inputYear,
            fromDate,
            toDate,
            mangelfullt,
            kommentarer
        )

        val roller = rolleMapper.toRoles(azureGateway.getRollerForInnloggetSaksbehandler())
        if (!isLederVedtaksinstans(roller)) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not leder vedtaksinstans")
        }

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        if (fromDate != null && toDate != null) {
            return TotalResponseWithoutEnheter(
                anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByDatesForVedtaksinstansleder(
                    fromDate = fromDate,
                    toDate = toDate,
                    vedtaksinstansEnhet = enhet,
                    mangelfullt = mangelfullt ?: emptyList(),
                    kommentarer = kommentarer ?: emptyList(),
                )
            )
        } else {
            val year = getYear(inputYear)
            return TotalResponseWithoutEnheter(
                anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByYearForVedtaksinstansleder(
                    year = year,
                    vedtaksinstansEnhet = enhet,
                    mangelfullt = mangelfullt ?: emptyList(),
                    kommentarer = kommentarer ?: emptyList(),
                )
            )
        }
    }

    private fun getYear(inputYear: Int?): Year = if (inputYear != null) Year.of(inputYear) else Year.now()

}