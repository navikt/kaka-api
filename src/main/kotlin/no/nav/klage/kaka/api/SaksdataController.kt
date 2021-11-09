package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.*
import no.nav.klage.kaka.domain.kodeverk.Hjemmel
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logSaksdataMethodDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["kaka-api:saksdata"])
@RequestMapping("/saksdata")
class SaksdataController(
    private val saksdataService: SaksdataService
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    val innloggetSaksbehandler = "TODO"

    @GetMapping("/{id}")
    fun getSaksdata(
        @PathVariable("id") saksdataId: UUID
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::getSaksdata.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.getSaksdata(saksdataId, innloggetSaksbehandler).toSaksdataView()
    }

    @PostMapping
    fun createSaksdata(): SaksdataView {
        logSaksdataMethodDetails(
            ::createSaksdata.name,
            innloggetSaksbehandler,
            UUID.randomUUID(),
            logger
        )

        return saksdataService.createSaksdata(innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/klager")
    fun setKlager(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: KlagerInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setKlager.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setKlager(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/sakstype")
    fun setSakstype(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: SakstypeInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setSakstype.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setSakstype(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/tema")
    fun setTema(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: TemaInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setTema.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setTema(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/mottattvedtaksinstans")
    fun setMottattVedtaksinstans(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: DatoInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setMottattVedtaksinstans.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setMottattVedtaksinstans(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/vedtaksinstansenhet")
    fun setVedtaksinstansEnhet(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: VedtaksinstansEnhetInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setVedtaksinstansEnhet.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setVedtaksinstansEnhet(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/mottattklageinstans")
    fun setMottattKlageinstans(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: DatoInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setMottattKlageinstans.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setMottattKlageinstans(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }


    @PutMapping("/{id}/utfall")
    fun setUtfall(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: UtfallInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setUtfall.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setUtfall(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/hjemler")
    fun setHjemler(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: HjemlerInput
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::setHjemler.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setHjemler(
            saksdataId,
            input.value?.map { Hjemmel.of(it) }?.toSet() ?: emptySet(),
            innloggetSaksbehandler
        ).toSaksdataView()
    }

    @PostMapping("/{id}/fullfoer")
    fun fullfoerSaksdata(
        @PathVariable("id") saksdataId: UUID
    ): SaksdataView {
        logSaksdataMethodDetails(
            ::fullfoerSaksdata.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setAvsluttetAvSaksbehandler(saksdataId, innloggetSaksbehandler).toSaksdataView()
    }
}