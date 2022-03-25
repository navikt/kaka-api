package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.kodeverk.RadioValg
import no.nav.klage.kaka.domain.kodeverk.RadioValgRaadgivendeLege
import no.nav.klage.kaka.exceptions.KvalitetsvurderingNotFoundException
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.repositories.KvalitetsvurderingRepository
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class KvalitetsvurderingService(
    private val kvalitetsvurderingRepository: KvalitetsvurderingRepository,
    private val saksdataRepository: SaksdataRepository
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
        val kvalitetsvurdering = kvalitetsvurderingRepository.findById(kvalitetsvurderingId)
        if (kvalitetsvurdering.isEmpty) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $kvalitetsvurderingId")
        }
        return kvalitetsvurdering.get()
    }

    fun setKlageforberedelsenRadioValg(
        kvalitetsvurderingId: UUID,
        input: RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.klageforberedelsenRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setSakensDokumenter(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.sakensDokumenter = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setOversittetKlagefristIkkeKommentert(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.oversittetKlagefristIkkeKommentert = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setKlagerensRelevanteAnfoerslerIkkeKommentert(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setBegrunnelseForHvorforAvslagOpprettholdes(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setKonklusjonen(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.konklusjonen = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }


    fun setOversendelsesbrevetsInnholdIkkeISamsvarMedTema(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenRadioValg(
        kvalitetsvurderingId: UUID,
        input: RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvMedisinskeForhold(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvMedisinskeForhold = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvMedisinskeForholdText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvMedisinskeForholdText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvInntektsforhold(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvInntektsforhold = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvInntektsforholdText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvInntektsforholdText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvArbeid(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvArbeid = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvArbeidText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvArbeidText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setArbeidsrettetBrukeroppfoelging(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.arbeidsrettetBrukeroppfoelging = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setArbeidsrettetBrukeroppfoelgingText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISaken(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvAndreAktuelleForholdISakenText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvEoesProblematikk(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvEoesProblematikk = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setUtredningenAvEoesProblematikkText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.utredningenAvEoesProblematikkText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVeiledningFraNav(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.veiledningFraNav = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukAvRaadgivendeLegeRadioValg(
        kvalitetsvurderingId: UUID,
        input: RadioValgRaadgivendeLege,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVeiledningFraNavText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.veiledningFraNavText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErIkkeBrukt(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeErIkkeBrukt = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErBruktFeilSpoersmaal(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeHarUttaltSegUtoverTrygdemedisin(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRaadgivendeLegeErBruktMangelfullDokumentasjon(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVedtaketRadioValg(
        kvalitetsvurderingId: UUID,
        input: RadioValg,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.vedtaketRadioValg = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setDetErIkkeBruktRiktigHjemmel(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.detErIkkeBruktRiktigHjemmel = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setRettsregelenErBenyttetFeil(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.rettsregelenErBenyttetFeil = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setVurderingAvFaktumErMangelfull(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.vurderingAvFaktumErMangelfull = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setDetErFeilIKonkretRettsanvendelse(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.detErFeilIKonkretRettsanvendelse = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBegrunnelsenErIkkeKonkretOgIndividuell(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setSpraaketErIkkeTydelig(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.spraaketErIkkeTydelig = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setNyeOpplysningerMottatt(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.nyeOpplysningerMottatt = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukIOpplaering(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.brukIOpplaering = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBrukIOpplaeringText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.brukIOpplaeringText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBetydeligAvvik(
        kvalitetsvurderingId: UUID,
        input: Boolean,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.betydeligAvvik = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun setBetydeligAvvikText(
        kvalitetsvurderingId: UUID,
        input: String,
        innloggetSaksbehandler: String
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)
        kvalitetsvurdering.betydeligAvvikText = input
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun cleanUpKvalitetsvurdering(
        kvalitetsvurderingId: UUID
    ) {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.cleanup()
        kvalitetsvurdering.modified = LocalDateTime.now()
    }

    fun removeFieldsUnusedInAnke(
        kvalitetsvurderingId: UUID
    ) {
        val kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
        kvalitetsvurdering.removeFieldsUnusedInAnke()
        kvalitetsvurdering.modified = LocalDateTime.now()
    }


    private fun getKvalitetsvurderingAndVerifyNotFinalized(
        kvalitetsvurderingId: UUID
    ): Kvalitetsvurdering {
        val kvalitetsvurdering = kvalitetsvurderingRepository.findById(kvalitetsvurderingId)
        if (kvalitetsvurdering.isEmpty) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $kvalitetsvurderingId")
        }
        return kvalitetsvurdering.get()
            .also {
                val saksdata = saksdataRepository.findOneByKvalitetsvurderingId(it.id)
                if (saksdata?.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException(
                    "Saksdata er allerede fullført"
                )
            }
    }
}