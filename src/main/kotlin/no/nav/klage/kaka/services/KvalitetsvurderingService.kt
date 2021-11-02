package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.repositories.KvalitetsvurderingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class KvalitetsvurderingService(
    private val kvalitetsvurderingRepository: KvalitetsvurderingRepository
) {

    fun setKlageforberedelsenRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.klageforberedelsenRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setSakensDokumenter(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.sakensDokumenter = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setOversittetKlagefristIkkeKommentert(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.oversittetKlagefristIkkeKommentert = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setKlagerensRelevanteAnfoerslerIkkeKommentert(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setBegrunnelseForHvorforAvslagOpprettholdes(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setKonklusjonen(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.konklusjonen = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvMedisinskeForhold(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvMedisinskeForhold = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvMedisinskeForholdText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvMedisinskeForholdText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvInntektsforhold(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvInntektsforhold = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvInntektsforholdText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvInntektsforholdText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvArbeid(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvArbeid = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvArbeidText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvArbeidText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setArbeidsrettetBrukeroppfoelging(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.arbeidsrettetBrukeroppfoelging = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setArbeidsrettetBrukeroppfoelgingText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISaken(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISakenText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvEoesProblematikk(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvEoesProblematikk = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvEoesProblematikkText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.utredningenAvEoesProblematikkText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVeiledningFraNav(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.veiledningFraNav = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukAvRaadgivendeLegeRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValgRaadgivendeLege,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVeiledningFraNavText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.veiledningFraNavText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErIkkeBrukt(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.raadgivendeLegeErIkkeBrukt = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErBruktFeilSpoersmaal(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErBruktMangelfullDokumentasjon(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVedtaketRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.vedtaketRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setDetErIkkeBruktRiktigHjemmel(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.detErIkkeBruktRiktigHjemmel = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRettsregelenErBenyttetFeil(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.rettsregelenErBenyttetFeil = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVurderingAvFaktumErMangelfull(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.vurderingAvFaktumErMangelfull = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setDetErFeilIKonkretRettsanvendelse(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.detErFeilIKonkretRettsanvendelse = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBegrunnelsenErIkkeKonkretOgIndividuell(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setSpraaketErIkkeTydelig(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.spraaketErIkkeTydelig = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setNyeOpplysningerMottatt(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.nyeOpplysningerMottatt = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukIOpplaering(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.brukIOpplaering = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukIOpplaeringText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.brukIOpplaeringText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBetydeligAvvik(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.betydeligAvvik = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBetydeligAvvikText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyAccess(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.betydeligAvvikText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    private fun getKvalitetsvurderingAndVerifyAccess(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        return kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
            .also { it.verifyAccess(innloggetSaksbehandler) }

    }
}