package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.MyResponseV3
import no.nav.klage.kaka.api.view.OpenResponseWithoutEnheterV3
import no.nav.klage.kaka.api.view.TotalResponseV3
import no.nav.klage.kaka.api.view.VedtaksinstanslederResponseV3
import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.ExportServiceV3
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
@Tag(name = "kaka-api:kaka-export-v3")
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
@RequestMapping("/statistics/v3")
class ExportControllerV3(
    private val exportServiceV3: ExportServiceV3,
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
    ): MyResponseV3 {
        logger.debug("getMyStats() called. FromDate = $fromDate, toDate = $toDate")

        val innloggetSaksbehandler = tokenUtil.getIdent()

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        val data = exportServiceV3.getFinishedAsRawDataByDatesAndKlageenhetPartitionedBySaksbehandler(
            fromDate = fromDate,
            toDate = toDate,
            enhet = enhet,
            saksbehandler = innloggetSaksbehandler,
        )
        return MyResponseV3(
            anonymizedFinishedVurderingList = data.mine,
            mine = data.mine,
            rest = data.rest,
        )
    }

    @GetMapping("/open")
    fun getOpen(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): OpenResponseWithoutEnheterV3 {
        logger.debug("getOpen() called. fromDate = $fromDate, toDate = $toDate")

        val anonymizedFinishedVurderingList = exportServiceV3.getFinishedAsRawDataByDatesWithoutEnheter(
            fromDate = fromDate,
            toDate = toDate
        )
        return OpenResponseWithoutEnheterV3(
            anonymizedFinishedVurderingList = anonymizedFinishedVurderingList,
            rest = anonymizedFinishedVurderingList,
        )
    }

    @GetMapping("/total")
    fun getTotal(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
    ): TotalResponseV3 {
        logger.debug("getTotal() called. FromDate = $fromDate, toDate = $toDate")

        val anonymizedFinishedVurderingList = exportServiceV3.getFinishedAsRawDataByDates(
            fromDate = fromDate,
            toDate = toDate
        )
        return TotalResponseV3(
            anonymizedFinishedVurderingList = anonymizedFinishedVurderingList,
            rest = anonymizedFinishedVurderingList,
        )
    }

    @GetMapping("/vedtaksinstansleder")
    fun getTotalForVedtaksinstansleder(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
        @RequestParam(required = false) mangelfullt: List<String>?,
    ): VedtaksinstanslederResponseV3 {
        logger.debug(
            "getTotalForVedtaksinstansleder() called. FromDate = {}, toDate = {}, mangelfullt = {}",
            fromDate,
            toDate,
            mangelfullt,
        )

        val roller = rolleMapper.toRoles(tokenUtil.getGroups())
        if (!isAllowedToReadKvalitetstilbakemeldinger(roller)) {
            throw MissingTilgangException("user ${tokenUtil.getIdent()} is not allowed to read kvalitetstilbakemeldinger")
        }

        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        val data = exportServiceV3.getFinishedAsRawDataByDatesForVedtaksinstansleder(
            fromDate = fromDate,
            toDate = toDate,
            vedtaksinstansEnhet = enhet,
            mangelfullt = mangelfullt ?: emptyList(),
        )
        return VedtaksinstanslederResponseV3(
            anonymizedFinishedVurderingList = data.mine,
            mine = data.mine,
            rest = data.rest,
        )
    }
}