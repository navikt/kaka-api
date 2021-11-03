package no.nav.klage.kaka.api

import no.nav.klage.kaka.api.view.*
import no.nav.klage.kaka.domain.Vurdering
import no.nav.klage.kaka.domain.kodeverk.Hjemmel
import no.nav.klage.kaka.services.VurderingService
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logVurderingMethodDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/vurdering")
class VurderingController(
    private val vurderingService: VurderingService
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    val innloggetSaksbehandler = "TODO"

    @GetMapping("/{id}")
    fun getVurdering(
        @PathVariable("id") vurderingId: UUID
    ): Vurdering {
        logVurderingMethodDetails(
            ::getVurdering.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.getVurdering(vurderingId, innloggetSaksbehandler)
    }

    @GetMapping
    fun getAllVurdering(): MutableList<Vurdering> {
        logVurderingMethodDetails(
            ::getAllVurdering.name,
            innloggetSaksbehandler,
            null,
            logger
        )

        return vurderingService.getAllVurdering()
    }

    @PostMapping
    fun createVurdering(): Vurdering {
        logVurderingMethodDetails(
            ::createVurdering.name,
            innloggetSaksbehandler,
            UUID.randomUUID(),
            logger
        )

        return vurderingService.createVurdering(innloggetSaksbehandler)
    }

    @PutMapping("/{id}/klager")
    fun setKlager(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: KlagerInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setKlager.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setKlager(vurderingId, input.klager, innloggetSaksbehandler)
    }

    @PutMapping("/{id}/sakstype")
    fun setSakstype(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: SakstypeInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setSakstype.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setSakstype(vurderingId, input.sakstype, innloggetSaksbehandler)
    }

    @PutMapping("/{id}/tema")
    fun setTema(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TemaInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setTema.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setTema(vurderingId, input.tema, innloggetSaksbehandler)
    }

    @PutMapping("/{id}/mottattvedtaksinstans")
    fun setMottattVedtaksinstans(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: DatoInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setMottattVedtaksinstans.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setMottattVedtaksinstans(vurderingId, input.dato, innloggetSaksbehandler)
    }

    @PutMapping("/{id}/vedtaksinstansenhet")
    fun setVedtaksinstansEnhet(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: VedtaksinstansEnhetInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setVedtaksinstansEnhet.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setVedtaksinstansEnhet(vurderingId, input.enhet, innloggetSaksbehandler)
    }

    @PutMapping("/{id}/mottattklageinstans")
    fun setMottattKlageinstans(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: DatoInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setMottattKlageinstans.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setMottattKlageinstans(vurderingId, input.dato, innloggetSaksbehandler)
    }


    @PutMapping("/{id}/utfall")
    fun setUtfall(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: UtfallInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtfall.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setUtfall(vurderingId, input.utfall, innloggetSaksbehandler)
    }

    @PutMapping("/{id}/hjemler")
    fun setHjemler(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: HjemlerInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setHjemler.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setHjemler(
            vurderingId,
            input.hjemler?.map { Hjemmel.of(it) }?.toSet() ?: emptySet(),
            innloggetSaksbehandler
        )
    }

    @PostMapping("/{id}/fullfoer")
    fun fullfoerVurdering(
        @PathVariable("id") vurderingId: UUID
    ): Vurdering {
        logVurderingMethodDetails(
            ::fullfoerVurdering.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return vurderingService.setAvsluttetAvSaksbehandler(vurderingId, innloggetSaksbehandler)
    }
}