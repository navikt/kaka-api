package no.nav.klage.kaka.api

import no.nav.klage.kaka.api.view.BooleanInput
import no.nav.klage.kaka.api.view.RadioValgInput
import no.nav.klage.kaka.api.view.RadioValgRaadgivendeLegeInput
import no.nav.klage.kaka.api.view.TextInput
import no.nav.klage.kaka.domain.Vurdering
import no.nav.klage.kaka.services.KvalitetsvurderingService
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logVurderingMethodDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/vurdering/{id}/kvalitetsvurdering")
class KvalitetsvurderingController(
    private val kvalitetsvurderingService: KvalitetsvurderingService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    val innloggetSaksbehandler = "TODO"

    @GetMapping("/test")
    fun test(
        @PathVariable("id") vurderingId: String
    ): String {
        return vurderingId
    }

    @PutMapping("/klageforberedelsenradiovalg")
    fun setKlageforberedelsenRadioValg(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: RadioValgInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setKlageforberedelsenRadioValg.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setKlageforberedelsenRadioValg(vurderingId, input.selection, innloggetSaksbehandler)
    }

    @PutMapping("/sakensdokumenter")
    fun setSakensDokumenter(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setSakensDokumenter.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setSakensDokumenter(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/oversittetklagefristikkekommentert")
    fun setOversittetKlagefristIkkeKommentert(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setOversittetKlagefristIkkeKommentert.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setOversittetKlagefristIkkeKommentert(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/klagerensrelevanteanfoerslerikkekommentert")
    fun setKlagerensRelevanteAnfoerslerIkkeKommentert(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setKlagerensRelevanteAnfoerslerIkkeKommentert.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setKlagerensRelevanteAnfoerslerIkkeKommentert(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/begrunnelseforhvorforavslagopprettholdes")
    fun setBegrunnelseForHvorforAvslagOpprettholdes(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBegrunnelseForHvorforAvslagOpprettholdes.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBegrunnelseForHvorforAvslagOpprettholdes(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/konklusjonen")
    fun setKonklusjonen(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setKonklusjonen.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setKonklusjonen(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/oversendelsesbrevetsinnholdikkeisamsvarmedtema")
    fun setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setOversendelsesbrevetsInnholdIkkeISamsvarMedTema.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenradiovalg")
    fun setUtredningenRadioValg(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: RadioValgInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenRadioValg.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenRadioValg(vurderingId, input.selection, innloggetSaksbehandler)
    }

    @PutMapping("/utredningenavmedisinskeforhold")
    fun setUtredningenAvMedisinskeForhold(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvMedisinskeForhold.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvMedisinskeForhold(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavmedisinskeforholdtext")
    fun setUtredningenAvMedisinskeForholdText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvMedisinskeForholdText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvMedisinskeForholdText(
            vurderingId,
            input.text,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavinntektsforhold")
    fun setUtredningenAvInntektsforhold(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvInntektsforhold.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvInntektsforhold(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavinntektsforholdtext")
    fun setUtredningenAvInntektsforholdText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvInntektsforholdText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvInntektsforholdText(
            vurderingId,
            input.text,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavarbeid")
    fun setUtredningenAvArbeid(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvArbeid.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvArbeid(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/utredningenavarbeidtext")
    fun setUtredningenAvArbeidText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvArbeidText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvArbeidText(vurderingId, input.text, innloggetSaksbehandler)
    }

    @PutMapping("/arbeidsrettetbrukeroppfoelging")
    fun setArbeidsrettetBrukeroppfoelging(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setArbeidsrettetBrukeroppfoelging.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setArbeidsrettetBrukeroppfoelging(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/arbeidsrettetbrukeroppfoelgingtext")
    fun setArbeidsrettetBrukeroppfoelgingText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setArbeidsrettetBrukeroppfoelgingText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setArbeidsrettetBrukeroppfoelgingText(
            vurderingId,
            input.text,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavandreaktuelleforholdisaken")
    fun setUtredningenAvAndreAktuelleForholdISaken(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvAndreAktuelleForholdISaken.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvAndreAktuelleForholdISaken(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavandreaktuelleforholdisakentext")
    fun setUtredningenAvAndreAktuelleForholdISakenText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvAndreAktuelleForholdISakenText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvAndreAktuelleForholdISakenText(
            vurderingId,
            input.text,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenaveoesproblematikk")
    fun setUtredningenAvEoesProblematikk(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvEoesProblematikk.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvEoesProblematikk(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenaveoesproblematikktext")
    fun setUtredningenAvEoesProblematikkText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setUtredningenAvEoesProblematikkText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvEoesProblematikkText(
            vurderingId,
            input.text,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/veiledningfranav")
    fun setVeiledningFraNav(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setVeiledningFraNav.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setVeiledningFraNav(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/veiledningfranavtext")
    fun setVeiledningFraNavText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setVeiledningFraNavText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setVeiledningFraNavText(vurderingId, input.text, innloggetSaksbehandler)
    }

    @PutMapping("/brukavraadgivendelegeradiovalg")
    fun setBrukAvRaadgivendeLegeRadioValg(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: RadioValgRaadgivendeLegeInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBrukAvRaadgivendeLegeRadioValg.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBrukAvRaadgivendeLegeRadioValg(vurderingId, input.selection, innloggetSaksbehandler)
    }

    @PutMapping("/raadgivendelegeerikkebrukt")
    fun setRaadgivendeLegeErIkkeBrukt(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setRaadgivendeLegeErIkkeBrukt.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeErIkkeBrukt(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeerbruktfeilspoersmaal")
    fun setRaadgivendeLegeErBruktFeilSpoersmaal(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setRaadgivendeLegeErBruktFeilSpoersmaal.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeErBruktFeilSpoersmaal(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeharuttaltsegutovertrygdemedisin")
    fun setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeerbruktmangelfulldokumentasjon")
    fun setRaadgivendeLegeErBruktMangelfullDokumentasjon(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setRaadgivendeLegeErBruktMangelfullDokumentasjon.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeErBruktMangelfullDokumentasjon(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/vedtaketradiovalg")
    fun setVedtaketRadioValg(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: RadioValgInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setVedtaketRadioValg.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setVedtaketRadioValg(vurderingId, input.selection, innloggetSaksbehandler)
    }

    @PutMapping("/deterikkebruktriktighjemmel")
    fun setDetErIkkeBruktRiktigHjemmel(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setDetErIkkeBruktRiktigHjemmel.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setDetErIkkeBruktRiktigHjemmel(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/innholdetirettsregleneerikketilstrekkeligbeskrevet")
    fun setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/rettsregelenerbenyttetfeil")
    fun setRettsregelenErBenyttetFeil(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setRettsregelenErBenyttetFeil.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setRettsregelenErBenyttetFeil(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/vurderingavfaktumermangelfull")
    fun setVurderingAvFaktumErMangelfull(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setVurderingAvFaktumErMangelfull.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setVurderingAvFaktumErMangelfull(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/deterfeilikonkretrettsanvendelse")
    fun setDetErFeilIKonkretRettsanvendelse(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setDetErFeilIKonkretRettsanvendelse.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setDetErFeilIKonkretRettsanvendelse(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/begrunnelsenerikkekonkretogindividuell")
    fun setBegrunnelsenErIkkeKonkretOgIndividuell(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBegrunnelsenErIkkeKonkretOgIndividuell.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBegrunnelsenErIkkeKonkretOgIndividuell(
            vurderingId,
            input.selected,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/spraaketerikketydelig")
    fun setSpraaketErIkkeTydelig(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setSpraaketErIkkeTydelig.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setSpraaketErIkkeTydelig(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/nyeopplysningermottatt")
    fun setNyeOpplysningerMottatt(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setNyeOpplysningerMottatt.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setNyeOpplysningerMottatt(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/brukiopplaering")
    fun setBrukIOpplaering(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBrukIOpplaering.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBrukIOpplaering(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/brukiopplaeringtext")
    fun setBrukIOpplaeringText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBrukIOpplaeringText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBrukIOpplaeringText(vurderingId, input.text, innloggetSaksbehandler)
    }

    @PutMapping("/betydeligavvik")
    fun setBetydeligAvvik(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBetydeligAvvik.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBetydeligAvvik(vurderingId, input.selected, innloggetSaksbehandler)
    }

    @PutMapping("/betydeligavviktext")
    fun setBetydeligAvvikText(
        @PathVariable("id") vurderingId: UUID,
        @RequestBody input: TextInput
    ): Vurdering {
        logVurderingMethodDetails(
            ::setBetydeligAvvikText.name,
            innloggetSaksbehandler,
            vurderingId,
            logger
        )

        return kvalitetsvurderingService.setBetydeligAvvikText(vurderingId, input.text, innloggetSaksbehandler)
    }
}