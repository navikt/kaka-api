package no.nav.klage.kaka.api

import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.*
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.KvalitetsvurderingV2Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.ytelseTilRegistreringshjemlerV1
import no.nav.klage.kodeverk.hjemmel.ytelseTilRegistreringshjemlerV2
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@Profile("dev-gcp")
@RestController
@RequestMapping("mockdata")
class AddRandomDataController(
    private val saksdataRepository: SaksdataRepository,
    private val kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository,
    private val kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository,
    @Value("#{T(java.time.LocalDate).parse('\${KAKA_VERSION_2_DATE}')}")
    private val kakaVersion2Date: LocalDate,
) {

    @Unprotected
    @PostMapping("/addrandomsaksdata")
    fun addTestData(@RequestParam amount: Int) {
        repeat(amount) {
            saksdataRepository.save(getRandomSaksdata())
        }
    }

    private fun getKakaVersion(): Int {
        val kvalitetsvurderingVersion = if (LocalDate.now() >= kakaVersion2Date) {
            2
        } else {
            1
        }
        return kvalitetsvurderingVersion
    }

    private fun getRandomSaksdata(): Saksdata {
        val kakaVersion = getKakaVersion()
        val cohesiveTestData = getCohesiveTestData(kakaVersion)

        val mottattVedtaksinstans = LocalDate.of(2021, (1..10).random(), (1..28).random())
        val mottattKA = mottattVedtaksinstans.plusDays((1..30).random().toLong())
        val potentialEndDate = mottattKA.plusDays((1..108).random().toLong())
        val avsluttetAvSaksbehandler = if (potentialEndDate > LocalDate.now()) LocalDate.now() else potentialEndDate

        val kvalitetsvurderingId = when (kakaVersion) {
            1 -> {
                kvalitetsvurderingV1Repository.save(getRandomKvalitetsvurderingV1()).id
            }

            2 -> {
                kvalitetsvurderingV2Repository.save(getRandomKvalitetsvurderingV2(cohesiveTestData.hjemler!!)).id
            }

            else -> error("Wrong version")

        }

        return Saksdata(
            sakstype = cohesiveTestData.type,
            utfoerendeSaksbehandler = cohesiveTestData.ident,
            tilknyttetEnhet = cohesiveTestData.enhet,
            ytelse = cohesiveTestData.ytelse,
            vedtaksinstansEnhet = cohesiveTestData.vedtaksEnhet,
            utfall = cohesiveTestData.utfall,
            registreringshjemler = cohesiveTestData.hjemler,
            sakenGjelder = "66666666666",
            mottattVedtaksinstans = mottattVedtaksinstans,
            mottattKlageinstans = mottattKA,
            avsluttetAvSaksbehandler = avsluttetAvSaksbehandler.atStartOfDay(),
            source = Source.entries.toTypedArray().random(),

            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingId,
                version = kakaVersion,
            ),

            created = mottattKA.atStartOfDay(),
            modified = avsluttetAvSaksbehandler.atStartOfDay(),
            tilbakekreving = Random.nextBoolean(),
        )
    }

    private fun getRandomKvalitetsvurderingV1(): KvalitetsvurderingV1 {
        return KvalitetsvurderingV1(
            klageforberedelsenRadioValg = RadioValg.values().random(),
            sakensDokumenter = Random.nextBoolean(),
            oversittetKlagefristIkkeKommentert = Random.nextBoolean(),
            klagerensRelevanteAnfoerslerIkkeKommentert = Random.nextBoolean(),
            begrunnelseForHvorforAvslagOpprettholdes = Random.nextBoolean(),
            konklusjonen = Random.nextBoolean(),
            oversendelsesbrevetsInnholdIkkeISamsvarMedTema = Random.nextBoolean(),
            utredningenRadioValg = RadioValg.values().random(),
            utredningenAvMedisinskeForhold = Random.nextBoolean(),
            utredningenAvMedisinskeForholdText = "en beskrivende tekst",
            utredningenAvInntektsforhold = Random.nextBoolean(),
            utredningenAvInntektsforholdText = null,
            utredningenAvArbeid = Random.nextBoolean(),
            utredningenAvArbeidText = null,
            arbeidsrettetBrukeroppfoelging = Random.nextBoolean(),
            arbeidsrettetBrukeroppfoelgingText = null,
            utredningenAvAndreAktuelleForholdISaken = Random.nextBoolean(),
            utredningenAvAndreAktuelleForholdISakenText = null,
            utredningenAvEoesProblematikk = Random.nextBoolean(),
            utredningenAvEoesProblematikkText = null,
            veiledningFraNav = Random.nextBoolean(),
            veiledningFraNavText = null,
            brukAvRaadgivendeLegeRadioValg = RadioValgRaadgivendeLege.values().random(),
            raadgivendeLegeErIkkeBrukt = Random.nextBoolean(),
            raadgivendeLegeErBruktFeilSpoersmaal = Random.nextBoolean(),
            raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = Random.nextBoolean(),
            raadgivendeLegeErBruktMangelfullDokumentasjon = Random.nextBoolean(),
            vedtaketRadioValg = RadioValg.values().random(),
            detErIkkeBruktRiktigHjemmel = Random.nextBoolean(),
            innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = Random.nextBoolean(),
            rettsregelenErBenyttetFeil = Random.nextBoolean(),
            vurderingAvFaktumErMangelfull = Random.nextBoolean(),
            detErFeilIKonkretRettsanvendelse = Random.nextBoolean(),
            begrunnelsenErIkkeKonkretOgIndividuell = Random.nextBoolean(),
            spraaketErIkkeTydelig = Random.nextBoolean(),
            nyeOpplysningerMottatt = Random.nextBoolean(),
            brukIOpplaering = Random.nextBoolean(),
            brukIOpplaeringText = null,
            betydeligAvvik = Random.nextBoolean(),
            betydeligAvvikText = null,
        )
    }

    private fun getRandomKvalitetsvurderingV2(hjemler: Set<Registreringshjemmel>): KvalitetsvurderingV2 {
        return KvalitetsvurderingV2(
            klageforberedelsenSakensDokumenter = Random.nextBoolean(),
            klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = Random.nextBoolean(),
            klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = Random.nextBoolean(),
            klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = Random.nextBoolean(),
            klageforberedelsen = KvalitetsvurderingV2.Radiovalg.values().random(),
            klageforberedelsenOversittetKlagefristIkkeKommentert = Random.nextBoolean(),
            klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = Random.nextBoolean(),
            klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = Random.nextBoolean(),
            klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = Random.nextBoolean(),
            klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = Random.nextBoolean(),
            utredningen = KvalitetsvurderingV2.Radiovalg.values().random(),
            utredningenAvMedisinskeForhold = Random.nextBoolean(),
            utredningenAvInntektsforhold = Random.nextBoolean(),
            utredningenAvArbeidsaktivitet = Random.nextBoolean(),
            utredningenAvEoesUtenlandsproblematikk = Random.nextBoolean(),
            utredningenAvAndreAktuelleForholdISaken = Random.nextBoolean(),
            vedtaketLovbestemmelsenTolketFeil = Random.nextBoolean(),
            vedtaketLovbestemmelsenTolketFeilHjemlerList = setOf(hjemler.random()),
            vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = Random.nextBoolean(),
            vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = setOf(hjemler.random()),
            vedtaketFeilKonkretRettsanvendelse = Random.nextBoolean(),
            vedtaketFeilKonkretRettsanvendelseHjemlerList = setOf(hjemler.random()),
            vedtaketIkkeKonkretIndividuellBegrunnelse = Random.nextBoolean(),
            vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = Random.nextBoolean(),
            vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = Random.nextBoolean(),
            vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = Random.nextBoolean(),
            vedtaketAutomatiskVedtak = Random.nextBoolean(),
            vedtaket = KvalitetsvurderingV2.Radiovalg.values().random(),
            vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = Random.nextBoolean(),
            vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = setOf(hjemler.random()),
            vedtaketDetErLagtTilGrunnFeilFaktum = Random.nextBoolean(),
            vedtaketSpraakOgFormidlingErIkkeTydelig = Random.nextBoolean(),
            raadgivendeLegeIkkebrukt = Random.nextBoolean(),
            raadgivendeLegeMangelfullBrukAvRaadgivendeLege = Random.nextBoolean(),
            raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = Random.nextBoolean(),
            raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = Random.nextBoolean(),
            brukAvRaadgivendeLege = KvalitetsvurderingV2.RadiovalgRaadgivendeLege.values().random(),
            annetFritekst = null,
        )
    }

    private fun getCohesiveTestData(kakaVersion: Int): CohesiveTestData {
        val ytelse = Ytelse.values().random()
        val type = Type.values().filter { it != Type.ANKE_I_TRYGDERETTEN }.random()
        val data = CohesiveTestData(
            type = type,
            utfall = typeTilUtfall[type]!!.random(),
            ident = listOf("Z994862", "Z994863", "Z994864").random(),
            enhet = ytelseTilKlageenheter[ytelse]!!.random().navn,
            ytelse = ytelse,
            vedtaksEnhet = ytelseTilVedtaksenheter[ytelse]!!.random().navn,
        )
        when (kakaVersion) {
            1 -> {
                data.hjemler = setOf(
                    ytelseTilRegistreringshjemlerV1[ytelse]!!.random(),
                    ytelseTilRegistreringshjemlerV1[ytelse]!!.random()
                )
            }

            2 -> {
                data.hjemler = setOf(
                    ytelseTilRegistreringshjemlerV2[ytelse]!!.random(),
                    ytelseTilRegistreringshjemlerV2[ytelse]!!.random()
                )
            }

        }
        return data
    }

    data class CohesiveTestData(
        val type: Type,
        val utfall: Utfall,
        val ident: String,
        var hjemler: Set<Registreringshjemmel>? = null,
        val enhet: String,
        val ytelse: Ytelse,
        val vedtaksEnhet: String
    )
}