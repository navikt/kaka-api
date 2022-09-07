package no.nav.klage.kaka.api

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.RadioValg
import no.nav.klage.kaka.domain.kodeverk.RadioValgRaadgivendeLege
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.ytelseTilRegistreringshjemler
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import kotlin.random.Random

@Profile("dev-gcp")
@RestController
@RequestMapping("mockdata")
class AddRandomDataController(
    private val saksdataRepository: SaksdataRepository
) {

    @Unprotected
    @PostMapping("/addrandomsaksdata")
    fun addTestData(@RequestParam amount: Int) {
        repeat(amount) {
            saksdataRepository.save(getRandomSaksdata())
        }
    }

    private fun getRandomSaksdata(): Saksdata {
        val cohesiveTestData = getCohesiveTestData()

        val mottattVedtaksinstans = LocalDate.of(2021, (1..10).random(), (1..28).random())
        val mottattKA = mottattVedtaksinstans.plusDays((1..30).random().toLong())
        val potentialEndDate = mottattKA.plusDays((1..108).random().toLong())
        val avsluttetAvSaksbehandler = if (potentialEndDate > LocalDate.now()) LocalDate.now() else potentialEndDate

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
            source = Source.values().random(),
            kvalitetsvurdering = getRandomKvalitetsvurdering(),
            created = mottattKA.atStartOfDay(),
            modified = avsluttetAvSaksbehandler.atStartOfDay()
        )
    }

    private fun getRandomKvalitetsvurdering(): Kvalitetsvurdering {
        return Kvalitetsvurdering(
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

    private fun getCohesiveTestData(): CohesiveTestData {
        val ytelse = Ytelse.values().random()
        val type = Type.values().random()
        return CohesiveTestData(
            type = type,
            utfall = typeTilUtfall[type]!!.random(),
            ident = listOf("Z994862", "Z994863", "Z994864").random(),
            enhet = ytelseTilKlageenheter[ytelse]!!.random().navn,
            ytelse = ytelse,
            vedtaksEnhet = ytelseTilVedtaksenheter[ytelse]!!.random().navn,
            hjemler = setOf(ytelseTilRegistreringshjemler[ytelse]!!.random())
        )
    }

    data class CohesiveTestData(
        val type: Type,
        val utfall: Utfall,
        val ident: String,
        val hjemler: Set<Registreringshjemmel>,
        val enhet: String,
        val ytelse: Ytelse,
        val vedtaksEnhet: String
    )
}