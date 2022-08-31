package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.TotalResponse
import no.nav.klage.kaka.api.view.TotalResponseWithoutEnheter
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.isAllowedToReadKvalitetstilbakemeldinger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Tag(name = "kaka-api:kaka-export")
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
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): TotalResponse {
        logger.debug("getMyStats() called. FromDate = $fromDate, toDate = $toDate")

        val innloggetSaksbehandler = tokenUtil.getIdent()

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
    }

    @GetMapping("/statistics/open")
    fun getOpen(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): TotalResponseWithoutEnheter {
        logger.debug("getOpen() called. fromDate = $fromDate, toDate = $toDate")

        return TotalResponseWithoutEnheter(
            anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByDatesWithoutEnheter(
                fromDate = fromDate,
                toDate = toDate
            )
        )
    }

    @GetMapping("/statistics/total")
    fun getTotal(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): TotalResponse {
        logger.debug("getTotal() called. FromDate = $fromDate, toDate = $toDate")

        return TotalResponse(
            anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByDates(
                fromDate = fromDate,
                toDate = toDate
            ),
            anonymizedUnfinishedVurderingList = exportService.getUnfinishedAsRawDataByToDate(
                toDate = toDate
            )
        )
    }

    @GetMapping("/statistics/vedtaksinstansleder")
    fun getTotalForVedtaksinstansleder(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
        @RequestParam(required = false) mangelfullt: List<String>?,
        @RequestParam(required = false) kommentarer: List<String>?,
    ): TotalResponseWithoutEnheter {
        logger.debug(
            "getTotalForVedtaksinstansleder() called. FromDate = {}, toDate = {}, mangelfullt = {}, kommentarer = {}",
            fromDate,
            toDate,
            mangelfullt,
            kommentarer
        )

        val roller = rolleMapper.toRoles(tokenUtil.getGroups())
        if (!isAllowedToReadKvalitetstilbakemeldinger(roller)) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not allowed to read kvalitetstilbakemeldinger")
        }

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        return TotalResponseWithoutEnheter(
            anonymizedFinishedVurderingList = exportService.getFinishedAsRawDataByDatesForVedtaksinstansleder(
                fromDate = fromDate,
                toDate = toDate,
                vedtaksinstansEnhet = enhet,
                mangelfullt = mangelfullt ?: emptyList(),
                kommentarer = kommentarer ?: emptyList(),
            )
        )
    }

}