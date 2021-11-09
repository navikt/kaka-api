package no.nav.klage.kaka.api

import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.BooleanInput
import no.nav.klage.kaka.api.view.RadioValgInput
import no.nav.klage.kaka.api.view.RadioValgRaadgivendeLegeInput
import no.nav.klage.kaka.api.view.TextInput
import no.nav.klage.kaka.config.SecurityConfig.Companion.ISSUER_AAD
import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.services.KvalitetsvurderingService
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logKvalitetsvurderingMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["kaka-api:kvalitet"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/kvalitetsvurdering/{id}")
class KvalitetsvurderingController(
    private val kvalitetsvurderingService: KvalitetsvurderingService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    val innloggetSaksbehandler = "TODO"

    @GetMapping
    fun test(
        @PathVariable("id") kvalitetsvurderingId: UUID
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setKlageforberedelsenRadioValg.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )
        return kvalitetsvurderingService.getKvalitetsvurdering(kvalitetsvurderingId, innloggetSaksbehandler)
    }

    @PutMapping("/klageforberedelsenradiovalg")
    fun setKlageforberedelsenRadioValg(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: RadioValgInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setKlageforberedelsenRadioValg.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setKlageforberedelsenRadioValg(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/sakensdokumenter")
    fun setSakensDokumenter(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setSakensDokumenter.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setSakensDokumenter(kvalitetsvurderingId, input.value, innloggetSaksbehandler)
    }

    @PutMapping("/oversittetklagefristikkekommentert")
    fun setOversittetKlagefristIkkeKommentert(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setOversittetKlagefristIkkeKommentert.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setOversittetKlagefristIkkeKommentert(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/klagerensrelevanteanfoerslerikkekommentert")
    fun setKlagerensRelevanteAnfoerslerIkkeKommentert(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setKlagerensRelevanteAnfoerslerIkkeKommentert.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setKlagerensRelevanteAnfoerslerIkkeKommentert(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/begrunnelseforhvorforavslagopprettholdes")
    fun setBegrunnelseForHvorforAvslagOpprettholdes(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBegrunnelseForHvorforAvslagOpprettholdes.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBegrunnelseForHvorforAvslagOpprettholdes(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/konklusjonen")
    fun setKonklusjonen(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setKonklusjonen.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setKonklusjonen(kvalitetsvurderingId, input.value, innloggetSaksbehandler)
    }

    @PutMapping("/oversendelsesbrevetsinnholdikkeisamsvarmedtema")
    fun setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setOversendelsesbrevetsInnholdIkkeISamsvarMedTema.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenradiovalg")
    fun setUtredningenRadioValg(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: RadioValgInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenRadioValg.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenRadioValg(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavmedisinskeforhold")
    fun setUtredningenAvMedisinskeForhold(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvMedisinskeForhold.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvMedisinskeForhold(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavmedisinskeforholdtext")
    fun setUtredningenAvMedisinskeForholdText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvMedisinskeForholdText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvMedisinskeForholdText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavinntektsforhold")
    fun setUtredningenAvInntektsforhold(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvInntektsforhold.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvInntektsforhold(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavinntektsforholdtext")
    fun setUtredningenAvInntektsforholdText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvInntektsforholdText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvInntektsforholdText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavarbeid")
    fun setUtredningenAvArbeid(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvArbeid.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvArbeid(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavarbeidtext")
    fun setUtredningenAvArbeidText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvArbeidText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvArbeidText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/arbeidsrettetbrukeroppfoelging")
    fun setArbeidsrettetBrukeroppfoelging(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setArbeidsrettetBrukeroppfoelging.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setArbeidsrettetBrukeroppfoelging(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/arbeidsrettetbrukeroppfoelgingtext")
    fun setArbeidsrettetBrukeroppfoelgingText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setArbeidsrettetBrukeroppfoelgingText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setArbeidsrettetBrukeroppfoelgingText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavandreaktuelleforholdisaken")
    fun setUtredningenAvAndreAktuelleForholdISaken(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvAndreAktuelleForholdISaken.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvAndreAktuelleForholdISaken(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenavandreaktuelleforholdisakentext")
    fun setUtredningenAvAndreAktuelleForholdISakenText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvAndreAktuelleForholdISakenText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvAndreAktuelleForholdISakenText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenaveoesproblematikk")
    fun setUtredningenAvEoesProblematikk(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvEoesProblematikk.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvEoesProblematikk(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/utredningenaveoesproblematikktext")
    fun setUtredningenAvEoesProblematikkText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setUtredningenAvEoesProblematikkText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setUtredningenAvEoesProblematikkText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/veiledningfranav")
    fun setVeiledningFraNav(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setVeiledningFraNav.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setVeiledningFraNav(kvalitetsvurderingId, input.value, innloggetSaksbehandler)
    }

    @PutMapping("/veiledningfranavtext")
    fun setVeiledningFraNavText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setVeiledningFraNavText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setVeiledningFraNavText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/brukavraadgivendelegeradiovalg")
    fun setBrukAvRaadgivendeLegeRadioValg(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: RadioValgRaadgivendeLegeInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBrukAvRaadgivendeLegeRadioValg.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBrukAvRaadgivendeLegeRadioValg(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeerikkebrukt")
    fun setRaadgivendeLegeErIkkeBrukt(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setRaadgivendeLegeErIkkeBrukt.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeErIkkeBrukt(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeerbruktfeilspoersmaal")
    fun setRaadgivendeLegeErBruktFeilSpoersmaal(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setRaadgivendeLegeErBruktFeilSpoersmaal.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeErBruktFeilSpoersmaal(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeharuttaltsegutovertrygdemedisin")
    fun setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/raadgivendelegeerbruktmangelfulldokumentasjon")
    fun setRaadgivendeLegeErBruktMangelfullDokumentasjon(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setRaadgivendeLegeErBruktMangelfullDokumentasjon.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setRaadgivendeLegeErBruktMangelfullDokumentasjon(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/vedtaketradiovalg")
    fun setVedtaketRadioValg(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: RadioValgInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setVedtaketRadioValg.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setVedtaketRadioValg(kvalitetsvurderingId, input.value, innloggetSaksbehandler)
    }

    @PutMapping("/deterikkebruktriktighjemmel")
    fun setDetErIkkeBruktRiktigHjemmel(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setDetErIkkeBruktRiktigHjemmel.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setDetErIkkeBruktRiktigHjemmel(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/innholdetirettsregleneerikketilstrekkeligbeskrevet")
    fun setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/rettsregelenerbenyttetfeil")
    fun setRettsregelenErBenyttetFeil(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setRettsregelenErBenyttetFeil.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setRettsregelenErBenyttetFeil(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/vurderingavfaktumermangelfull")
    fun setVurderingAvFaktumErMangelfull(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setVurderingAvFaktumErMangelfull.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setVurderingAvFaktumErMangelfull(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/deterfeilikonkretrettsanvendelse")
    fun setDetErFeilIKonkretRettsanvendelse(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setDetErFeilIKonkretRettsanvendelse.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setDetErFeilIKonkretRettsanvendelse(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/begrunnelsenerikkekonkretogindividuell")
    fun setBegrunnelsenErIkkeKonkretOgIndividuell(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBegrunnelsenErIkkeKonkretOgIndividuell.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBegrunnelsenErIkkeKonkretOgIndividuell(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/spraaketerikketydelig")
    fun setSpraaketErIkkeTydelig(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setSpraaketErIkkeTydelig.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setSpraaketErIkkeTydelig(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/nyeopplysningermottatt")
    fun setNyeOpplysningerMottatt(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setNyeOpplysningerMottatt.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setNyeOpplysningerMottatt(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/brukiopplaering")
    fun setBrukIOpplaering(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBrukIOpplaering.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBrukIOpplaering(kvalitetsvurderingId, input.value, innloggetSaksbehandler)
    }

    @PutMapping("/brukiopplaeringtext")
    fun setBrukIOpplaeringText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBrukIOpplaeringText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBrukIOpplaeringText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }

    @PutMapping("/betydeligavvik")
    fun setBetydeligAvvik(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: BooleanInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBetydeligAvvik.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBetydeligAvvik(kvalitetsvurderingId, input.value, innloggetSaksbehandler)
    }

    @PutMapping("/betydeligavviktext")
    fun setBetydeligAvvikText(
        @PathVariable("id") kvalitetsvurderingId: UUID,
        @RequestBody input: TextInput
    ): Kvalitetsvurdering {
        logKvalitetsvurderingMethodDetails(
            ::setBetydeligAvvikText.name,
            innloggetSaksbehandler,
            kvalitetsvurderingId,
            logger
        )

        return kvalitetsvurderingService.setBetydeligAvvikText(
            kvalitetsvurderingId,
            input.value,
            innloggetSaksbehandler
        )
    }
}