package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import kotlin.random.Random

@ActiveProfiles("local")
@DataJpaTest
class KvalitetsvurderingV3RepositoryTest: PostgresIntegrationTestBase() {
    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var kvalitetsvurderingV3Repository: KvalitetsvurderingV3Repository

    @Test
    fun `add kvalitetsvurderingV3 works`() {
        val kvalitetsvurderingV3 = KvalitetsvurderingV3()
        kvalitetsvurderingV3Repository.save(kvalitetsvurderingV3)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundKvalitetsvurderingV3 = kvalitetsvurderingV3Repository.getReferenceById(kvalitetsvurderingV3.id)
        assertThat(foundKvalitetsvurderingV3).isEqualTo(kvalitetsvurderingV3)
    }

    @Test
    fun `add kvalitetsvurderingV3 with all values and verify`() {
        val kvalitetsvurderingV3 = KvalitetsvurderingV3(
            // Kvalitetsavvik i forvaltningen av særregelverket
            saerregelverkAutomatiskVedtak = Random.nextBoolean(),
            saerregelverk = KvalitetsvurderingV3.Radiovalg.entries.random(),
            saerregelverkLovenErTolketEllerAnvendtFeil = Random.nextBoolean(),
            saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = Random.nextBoolean(),
            saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = setOf(
                Registreringshjemmel.entries.random(),
                Registreringshjemmel.entries.random()
            ),
            saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = Random.nextBoolean(),
            saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = setOf(
                Registreringshjemmel.entries.random(),
                Registreringshjemmel.entries.random()
            ),
            saerregelverkDetErLagtTilGrunnFeilFaktum = Random.nextBoolean(),
            saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = setOf(
                Registreringshjemmel.entries.random(),
                Registreringshjemmel.entries.random()
            ),

            // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
            saksbehandlingsregler = KvalitetsvurderingV3.Radiovalg.entries.random(),
            saksbehandlingsreglerBruddPaaVeiledningsplikten = Random.nextBoolean(),
            saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = Random.nextBoolean(),
            saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaUtredningsplikten = Random.nextBoolean(),
            saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = Random.nextBoolean(),
            saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = Random.nextBoolean(),
            saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = Random.nextBoolean(),
            saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = Random.nextBoolean(),
            saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = Random.nextBoolean(),
            saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaForeleggelsesplikten = Random.nextBoolean(),
            saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = Random.nextBoolean(),
            saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaBegrunnelsesplikten = Random.nextBoolean(),
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = Random.nextBoolean(),
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = setOf(
                Registreringshjemmel.entries.random(),
                Registreringshjemmel.entries.random()
            ),
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = Random.nextBoolean(),
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = setOf(
                Registreringshjemmel.entries.random(),
                Registreringshjemmel.entries.random()
            ),
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = Random.nextBoolean(),
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = setOf(
                Registreringshjemmel.entries.random(),
                Registreringshjemmel.entries.random()
            ),
            saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = Random.nextBoolean(),
            saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = Random.nextBoolean(),
            saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaJournalfoeringsplikten = Random.nextBoolean(),
            saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = Random.nextBoolean(),
            saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = Random.nextBoolean(),
            saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = Random.nextBoolean(),

            // Kvalitetsavvik i saker med trygdemedisin
            brukAvRaadgivendeLege = KvalitetsvurderingV3.RadiovalgRaadgivendeLege.entries.random(),
            raadgivendeLegeIkkebrukt = Random.nextBoolean(),
            raadgivendeLegeMangelfullBrukAvRaadgivendeLege = Random.nextBoolean(),
            raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = Random.nextBoolean(),
            raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = Random.nextBoolean(),

            // Annet
            annetFritekst = "Test fritekst",
        )
        kvalitetsvurderingV3Repository.save(kvalitetsvurderingV3)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundKvalitetsvurderingV3 = kvalitetsvurderingV3Repository.getReferenceById(kvalitetsvurderingV3.id)

        // Særregelverk assertions
        assertThat(foundKvalitetsvurderingV3.saerregelverkAutomatiskVedtak).isEqualTo(kvalitetsvurderingV3.saerregelverkAutomatiskVedtak)
        assertThat(foundKvalitetsvurderingV3.saerregelverk).isEqualTo(kvalitetsvurderingV3.saerregelverk)
        assertThat(foundKvalitetsvurderingV3.saerregelverkLovenErTolketEllerAnvendtFeil).isEqualTo(kvalitetsvurderingV3.saerregelverkLovenErTolketEllerAnvendtFeil)
        assertThat(foundKvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning).isEqualTo(
            kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning
        )
        assertThat(foundKvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList).isEqualTo(
            kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList
        )
        assertThat(foundKvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn).isEqualTo(
            kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn
        )
        assertThat(foundKvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList).isEqualTo(
            kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList
        )
        assertThat(foundKvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktum).isEqualTo(kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktum)
        assertThat(foundKvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList).isEqualTo(
            kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList
        )

        // Saksbehandlingsregler assertions
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsregler).isEqualTo(kvalitetsvurderingV3.saksbehandlingsregler)
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaVeiledningsplikten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaVeiledningsplikten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaUtredningsplikten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaUtredningsplikten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaForeleggelsesplikten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaForeleggelsesplikten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaBegrunnelsesplikten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaBegrunnelsesplikten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaJournalfoeringsplikten).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaJournalfoeringsplikten
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok
        )
        assertThat(foundKvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok).isEqualTo(
            kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok
        )

        // Trygdemedisin assertions
        assertThat(foundKvalitetsvurderingV3.brukAvRaadgivendeLege).isEqualTo(kvalitetsvurderingV3.brukAvRaadgivendeLege)
        assertThat(foundKvalitetsvurderingV3.raadgivendeLegeIkkebrukt).isEqualTo(kvalitetsvurderingV3.raadgivendeLegeIkkebrukt)
        assertThat(foundKvalitetsvurderingV3.raadgivendeLegeMangelfullBrukAvRaadgivendeLege).isEqualTo(
            kvalitetsvurderingV3.raadgivendeLegeMangelfullBrukAvRaadgivendeLege
        )
        assertThat(foundKvalitetsvurderingV3.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin).isEqualTo(
            kvalitetsvurderingV3.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin
        )
        assertThat(foundKvalitetsvurderingV3.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert).isEqualTo(
            kvalitetsvurderingV3.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert
        )

        // Annet assertions
        assertThat(foundKvalitetsvurderingV3.annetFritekst).isEqualTo(kvalitetsvurderingV3.annetFritekst)
    }
}