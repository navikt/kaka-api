package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.*
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*
import kotlin.random.Random

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KvalitetsvurderingV2RepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository

    @Test
    fun `add kvalitetsvurderingV2 works`() {
        val kvalitetsvurderingV2 = KvalitetsvurderingV2()
        kvalitetsvurderingV2Repository.save(kvalitetsvurderingV2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundKvalitetsvurderingV2 = kvalitetsvurderingV2Repository.getReferenceById(kvalitetsvurderingV2.id)
        assertThat(foundKvalitetsvurderingV2).isEqualTo(kvalitetsvurderingV2)
    }

    @Test
    fun `add kvalitetsvurderingV2 with complete valueset`() {
        val kvalitetsvurderingV2 = KvalitetsvurderingV2(
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
            vedtaketLovbestemmelsenTolketFeilHjemlerList = setOf(
                Registreringshjemmel.values().random(),
                Registreringshjemmel.values().random()
            ),
            vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = Random.nextBoolean(),
            vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = setOf(
                Registreringshjemmel.values().random(), Registreringshjemmel.values().random()
            ),
            vedtaketFeilKonkretRettsanvendelse = Random.nextBoolean(),
            vedtaketFeilKonkretRettsanvendelseHjemlerList = setOf(
                Registreringshjemmel.values().random(),
                Registreringshjemmel.values().random()
            ),
            vedtaketIkkeKonkretIndividuellBegrunnelse = Random.nextBoolean(),
            vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = Random.nextBoolean(),
            vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = Random.nextBoolean(),
            vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = Random.nextBoolean(),
            vedtaketAutomatiskVedtak = Random.nextBoolean(),
            vedtaket = KvalitetsvurderingV2.Radiovalg.values().random(),
            vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = Random.nextBoolean(),
            vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = setOf(
                Registreringshjemmel.values().random(), Registreringshjemmel.values().random()
            ),
            vedtaketDetErLagtTilGrunnFeilFaktum = Random.nextBoolean(),
            vedtaketSpraakOgFormidlingErIkkeTydelig = Random.nextBoolean(),
            raadgivendeLegeIkkebrukt = Random.nextBoolean(),
            raadgivendeLegeMangelfullBrukAvRaadgivendeLege = Random.nextBoolean(),
            raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = Random.nextBoolean(),
            raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = Random.nextBoolean(),
            brukAvRaadgivendeLege = KvalitetsvurderingV2.RadiovalgRaadgivendeLege.values().random(),
            annetFritekst = null,
        )
        kvalitetsvurderingV2Repository.save(kvalitetsvurderingV2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundKvalitetsvurderingV2 = kvalitetsvurderingV2Repository.getReferenceById(kvalitetsvurderingV2.id)

        assertThat(foundKvalitetsvurderingV2.klageforberedelsenSakensDokumenter).isEqualTo(kvalitetsvurderingV2.klageforberedelsenSakensDokumenter)
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsen).isEqualTo(kvalitetsvurderingV2.klageforberedelsen)
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema
        )
        assertThat(foundKvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker).isEqualTo(
            kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker
        )
        assertThat(foundKvalitetsvurderingV2.utredningen).isEqualTo(kvalitetsvurderingV2.utredningen)
        assertThat(foundKvalitetsvurderingV2.utredningenAvMedisinskeForhold).isEqualTo(kvalitetsvurderingV2.utredningenAvMedisinskeForhold)
        assertThat(foundKvalitetsvurderingV2.utredningenAvInntektsforhold).isEqualTo(kvalitetsvurderingV2.utredningenAvInntektsforhold)
        assertThat(foundKvalitetsvurderingV2.utredningenAvArbeidsaktivitet).isEqualTo(kvalitetsvurderingV2.utredningenAvArbeidsaktivitet)
        assertThat(foundKvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk).isEqualTo(kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk)
        assertThat(foundKvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken).isEqualTo(kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken)
        assertThat(foundKvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil).isEqualTo(kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil)
        assertThat(foundKvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList).isEqualTo(
            kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert).isEqualTo(
            kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList).isEqualTo(
            kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse).isEqualTo(kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse)
        assertThat(foundKvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList).isEqualTo(
            kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse).isEqualTo(kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse)
        assertThat(foundKvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum).isEqualTo(
            kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum).isEqualTo(
            kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst).isEqualTo(
            kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketAutomatiskVedtak).isEqualTo(kvalitetsvurderingV2.vedtaketAutomatiskVedtak)
        assertThat(foundKvalitetsvurderingV2.vedtaket).isEqualTo(kvalitetsvurderingV2.vedtaket)
        assertThat(foundKvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet).isEqualTo(
            kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList).isEqualTo(
            kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList
        )
        assertThat(foundKvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum).isEqualTo(kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum)
        assertThat(foundKvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig).isEqualTo(kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig)
        assertThat(foundKvalitetsvurderingV2.raadgivendeLegeIkkebrukt).isEqualTo(kvalitetsvurderingV2.raadgivendeLegeIkkebrukt)
        assertThat(foundKvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege).isEqualTo(
            kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege
        )
        assertThat(foundKvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin).isEqualTo(
            kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin
        )
        assertThat(foundKvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert).isEqualTo(
            kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert
        )
        assertThat(foundKvalitetsvurderingV2.brukAvRaadgivendeLege).isEqualTo(kvalitetsvurderingV2.brukAvRaadgivendeLege)
        assertThat(foundKvalitetsvurderingV2.annetFritekst).isEqualTo(kvalitetsvurderingV2.annetFritekst)
    }
}