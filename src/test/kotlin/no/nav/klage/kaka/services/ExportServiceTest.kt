package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.RadioValg
import no.nav.klage.kaka.domain.kodeverk.RadioValgRaadgivendeLege
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year


internal class ExportServiceTest {

    @Test
    fun generateExcelFileAsLeder() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                any(),
                any()
            )
        } returns getSaksdata(amount = 10)

        val exportService = ExportService(saksdataRepository)

        val currDir = File(".")
        val path: String = currDir.absolutePath
        val fileLocation = path.substring(0, path.length - 1) + "testfile.xlsx"

        File(fileLocation).writeBytes(
            exportService.getAsExcel(
                year = Year.now()
            )
        )
    }

    @Test
    fun `generate excel file with no data does not throw exception`() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                any(),
                any()
            )
        } returns getSaksdata(amount = 0)

        val exportService = ExportService(saksdataRepository)

        val currDir = File(".")
        val path: String = currDir.absolutePath
        val fileLocation = path.substring(0, path.length - 1) + "testfile.xlsx"

        File(fileLocation).writeBytes(
            exportService.getAsExcel(
                year = Year.now()
            )
        )
    }

    private fun getSaksdata(amount: Int = 1): List<Saksdata> {
        return buildList {
            repeat(amount) {
                add(
                    Saksdata(
                        sakstype = Type.KLAGE,
                        utfoerendeSaksbehandler = "someoneelse",
                        tilknyttetEnhet = Enhet.E4295.navn,
                        ytelse = Ytelse.OMS_OMP,
                        vedtaksinstansEnhet = Enhet.E0001.navn,
                        utfall = Utfall.STADFESTELSE,
                        registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4, Registreringshjemmel.FTRL_9_11),
                        sakenGjelder = "12345678910",
                        mottattVedtaksinstans = LocalDate.now(),
                        mottattKlageinstans = LocalDate.now().minusDays(1),
                        avsluttetAvSaksbehandler = LocalDateTime.now(),
                        source = Source.KAKA,
                        kvalitetsvurdering = Kvalitetsvurdering(
                            klageforberedelsenRadioValg = RadioValg.MANGELFULLT,
                            sakensDokumenter = false,
                            oversittetKlagefristIkkeKommentert = false,
                            klagerensRelevanteAnfoerslerIkkeKommentert = false,
                            begrunnelseForHvorforAvslagOpprettholdes = false,
                            konklusjonen = false,
                            oversendelsesbrevetsInnholdIkkeISamsvarMedTema = false,
                            utredningenRadioValg = RadioValg.MANGELFULLT,
                            utredningenAvMedisinskeForhold = false,
                            utredningenAvMedisinskeForholdText = "en beskrivende tekst",
                            utredningenAvInntektsforhold = false,
                            utredningenAvInntektsforholdText = null,
                            utredningenAvArbeid = false,
                            utredningenAvArbeidText = null,
                            arbeidsrettetBrukeroppfoelging = false,
                            arbeidsrettetBrukeroppfoelgingText = null,
                            utredningenAvAndreAktuelleForholdISaken = false,
                            utredningenAvAndreAktuelleForholdISakenText = null,
                            utredningenAvEoesProblematikk = false,
                            utredningenAvEoesProblematikkText = null,
                            veiledningFraNav = false,
                            veiledningFraNavText = null,
                            brukAvRaadgivendeLegeRadioValg = RadioValgRaadgivendeLege.MANGELFULLT,
                            raadgivendeLegeErIkkeBrukt = false,
                            raadgivendeLegeErBruktFeilSpoersmaal = false,
                            raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = false,
                            raadgivendeLegeErBruktMangelfullDokumentasjon = false,
                            vedtaketRadioValg = RadioValg.MANGELFULLT,
                            detErIkkeBruktRiktigHjemmel = false,
                            innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = false,
                            rettsregelenErBenyttetFeil = false,
                            vurderingAvFaktumErMangelfull = false,
                            detErFeilIKonkretRettsanvendelse = false,
                            begrunnelsenErIkkeKonkretOgIndividuell = false,
                            spraaketErIkkeTydelig = false,
                            nyeOpplysningerMottatt = false,
                            brukIOpplaering = false,
                            brukIOpplaeringText = null,
                            betydeligAvvik = false,
                            betydeligAvvikText = null,
                        )
                    )
                )
            }
        }
    }
}