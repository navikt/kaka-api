package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.exceptions.KvalitetsvurderingFinalizedException
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

    fun createKvalitetsvurdering(): Kvalitetsvurdering {
        return kvalitetsvurderingRepository.save(
            Kvalitetsvurdering()
        )
    }

    fun getKvalitetsvurdering(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        return kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
    }

    fun setKlageforberedelsenRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.klageforberedelsenRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setSakensDokumenter(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.sakensDokumenter = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setOversittetKlagefristIkkeKommentert(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.oversittetKlagefristIkkeKommentert = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setKlagerensRelevanteAnfoerslerIkkeKommentert(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setBegrunnelseForHvorforAvslagOpprettholdes(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setKonklusjonen(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.konklusjonen = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvMedisinskeForhold(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvMedisinskeForhold = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvMedisinskeForholdText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvMedisinskeForholdText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvInntektsforhold(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvInntektsforhold = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvInntektsforholdText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvInntektsforholdText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvArbeid(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvArbeid = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvArbeidText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvArbeidText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setArbeidsrettetBrukeroppfoelging(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.arbeidsrettetBrukeroppfoelging = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setArbeidsrettetBrukeroppfoelgingText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISaken(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISakenText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvEoesProblematikk(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvEoesProblematikk = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvEoesProblematikkText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvEoesProblematikkText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVeiledningFraNav(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.veiledningFraNav = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukAvRaadgivendeLegeRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValgRaadgivendeLege,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVeiledningFraNavText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.veiledningFraNavText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErIkkeBrukt(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeErIkkeBrukt = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErBruktFeilSpoersmaal(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErBruktMangelfullDokumentasjon(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVedtaketRadioValg(
        kvalitetsvurderingId: UUID,
        input: Kvalitetsvurdering.RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.vedtaketRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setDetErIkkeBruktRiktigHjemmel(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.detErIkkeBruktRiktigHjemmel = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRettsregelenErBenyttetFeil(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.rettsregelenErBenyttetFeil = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVurderingAvFaktumErMangelfull(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.vurderingAvFaktumErMangelfull = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setDetErFeilIKonkretRettsanvendelse(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.detErFeilIKonkretRettsanvendelse = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBegrunnelsenErIkkeKonkretOgIndividuell(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setSpraaketErIkkeTydelig(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.spraaketErIkkeTydelig = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setNyeOpplysningerMottatt(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.nyeOpplysningerMottatt = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukIOpplaering(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.brukIOpplaering = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukIOpplaeringText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.brukIOpplaeringText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBetydeligAvvik(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.betydeligAvvik = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBetydeligAvvikText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.betydeligAvvikText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun finalizeKvalitetsvurdering(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ) {
        val kvalitetsvurdering =
            getKvalitetsvurderingAndVerifyAccessForEdit(kvalitetsvurderingId, innloggetSaksbehandler)
        kvalitetsvurdering.cleanup()
        kvalitetsvurdering.modified = LocalDateTime.now()
        kvalitetsvurdering.avsluttetAvSaksbehandler = LocalDateTime.now()
    }

    private fun getKvalitetsvurderingAndVerifyAccess(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        return kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
            .also { it.verifyAccess(innloggetSaksbehandler) }
    }

    private fun getKvalitetsvurderingAndVerifyAccessForEdit(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        return kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
            .also { it.verifyAccess(innloggetSaksbehandler) }
            .also { if (it.avsluttetAvSaksbehandler != null) throw KvalitetsvurderingFinalizedException("Kvalitetsvurderingen er allerede fullført") }
    }
}