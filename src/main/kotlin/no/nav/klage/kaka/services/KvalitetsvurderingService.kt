package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Vurdering
import no.nav.klage.kaka.repositories.VurderingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class KvalitetsvurderingService(
    private val vurderingRepository: VurderingRepository
) {

    fun setKlageforberedelsenRadioValg(vurderingId: UUID, input: Kvalitetsvurdering.RadioValg, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.klageforberedelsenRadioValg = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setSakensDokumenter(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.sakensDokumenter = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setOversittetKlagefristIkkeKommentert(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.oversittetKlagefristIkkeKommentert = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }


    fun setKlagerensRelevanteAnfoerslerIkkeKommentert(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }


    fun setBegrunnelseForHvorforAvslagOpprettholdes(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setKonklusjonen(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.konklusjonen = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }


    fun setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenRadioValg(vurderingId: UUID, input: Kvalitetsvurdering.RadioValg, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenRadioValg = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvMedisinskeForhold(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvMedisinskeForhold = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvMedisinskeForholdText(
        vurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvMedisinskeForholdText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvInntektsforhold(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvInntektsforhold = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvInntektsforholdText(
        vurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvInntektsforholdText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvArbeid(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvArbeid = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvArbeidText(vurderingId: UUID, input: String, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvArbeidText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setArbeidsrettetBrukeroppfoelging(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.arbeidsrettetBrukeroppfoelging = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setArbeidsrettetBrukeroppfoelgingText(
        vurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISaken(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISakenText(
        vurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvEoesProblematikk(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvEoesProblematikk = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtredningenAvEoesProblematikkText(
        vurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.utredningenAvEoesProblematikkText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setVeiledningFraNav(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.veiledningFraNav = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setBrukAvRaadgivendeLegeRadioValg(vurderingId: UUID, input: Kvalitetsvurdering.RadioValgRaadgivendeLege, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setVeiledningFraNavText(vurderingId: UUID, input: String, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.veiledningFraNavText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setRaadgivendeLegeErIkkeBrukt(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.raadgivendeLegeErIkkeBrukt = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setRaadgivendeLegeErBruktFeilSpoersmaal(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setRaadgivendeLegeErBruktMangelfullDokumentasjon(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setVedtaketRadioValg(vurderingId: UUID, input: Kvalitetsvurdering.RadioValg, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.vedtaketRadioValg = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setDetErIkkeBruktRiktigHjemmel(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.detErIkkeBruktRiktigHjemmel = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setRettsregelenErBenyttetFeil(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.rettsregelenErBenyttetFeil = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setVurderingAvFaktumErMangelfull(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.vurderingAvFaktumErMangelfull = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setDetErFeilIKonkretRettsanvendelse(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.detErFeilIKonkretRettsanvendelse = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setBegrunnelsenErIkkeKonkretOgIndividuell(
        vurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setSpraaketErIkkeTydelig(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.spraaketErIkkeTydelig = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setNyeOpplysningerMottatt(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.nyeOpplysningerMottatt = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setBrukIOpplaering(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.brukIOpplaering = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setBrukIOpplaeringText(vurderingId: UUID, input: String, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.brukIOpplaeringText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setBetydeligAvvik(vurderingId: UUID, input: Boolean, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.betydeligAvvik = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setBetydeligAvvikText(vurderingId: UUID, input: String, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.kvalitetsvurdering.betydeligAvvikText = input
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    private fun getVurderingAndVerifyAccess(vurderingId: UUID, innloggetSaksbehandler: String): Vurdering {
        return vurderingRepository.getById(vurderingId).also { it.verifyAccess(innloggetSaksbehandler) }
    }
}