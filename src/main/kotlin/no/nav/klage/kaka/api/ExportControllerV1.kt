package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.MyResponseV1
import no.nav.klage.kaka.api.view.TotalResponseV1
import no.nav.klage.kaka.api.view.TotalResponseWithoutEnheterV1
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportServiceV1
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.isAllowedToReadKvalitetstilbakemeldinger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Tag(name = "kaka-api:kaka-export-v1")
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
@RequestMapping("/statistics", "/statistics/v1")
class ExportControllerV1(
    private val exportServiceV1: ExportServiceV1,
    private val tokenUtil: TokenUtil,
    private val azureGateway: AzureGateway,
    private val rolleMapper: RolleMapper,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/my")
    fun getMyStats(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): MyResponseV1 {
        logger.debug("getMyStats() called. FromDate = $fromDate, toDate = $toDate")

        val innloggetSaksbehandler = tokenUtil.getIdent()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        val data = exportServiceV1.getFinishedAsRawDataByDatesAndKlageenhetPartitionedBySaksbehandler(
            fromDate = fromDate,
            toDate = toDate,
            enhet = enhet,
            saksbehandler = innloggetSaksbehandler,
        )

        return MyResponseV1(
            anonymizedFinishedVurderingList = data.mine,
            mine = data.mine,
            rest = data.rest,
        )
    }

    @GetMapping("/open")
    fun getOpen(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): TotalResponseWithoutEnheterV1 {
        logger.debug("getOpen() called. fromDate = $fromDate, toDate = $toDate")

        return TotalResponseWithoutEnheterV1(
            anonymizedFinishedVurderingList = exportServiceV1.getFinishedAsRawDataByDatesWithoutEnheter(
                fromDate = fromDate,
                toDate = toDate
            )
        )
    }

    @GetMapping("/total")
    fun getTotal(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): TotalResponseV1 {
        logger.debug("getTotal() called. FromDate = $fromDate, toDate = $toDate")

        val anonymizedFinishedVurderingList = exportServiceV1.getFinishedAsRawDataByDates(
            fromDate = fromDate,
            toDate = toDate
        )
        return TotalResponseV1(
            anonymizedFinishedVurderingList = anonymizedFinishedVurderingList,
            rest = anonymizedFinishedVurderingList,
        )
    }

    @GetMapping("/vedtaksinstansleder")
    fun getTotalForVedtaksinstansleder(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
        @RequestParam(required = false) mangelfullt: List<String>?,
        @RequestParam(required = false) kommentarer: List<String>?,
    ): TotalResponseWithoutEnheterV1 {
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

        return TotalResponseWithoutEnheterV1(
            anonymizedFinishedVurderingList = exportServiceV1.getFinishedAsRawDataByDatesForVedtaksinstansleder(
                fromDate = fromDate,
                toDate = toDate,
                vedtaksinstansEnhet = enhet,
                mangelfullt = mangelfullt ?: emptyList(),
                kommentarer = kommentarer ?: emptyList(),
            )
        )
    }

}