package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.util.*


internal class ExportServiceV1Test {

    @Test
    fun generateExcelFileAsLeder() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV1(
                any(),
                any(),
            )
        } returns getResultList(amount = 10)

        val exportService = ExportServiceV1(
            saksdataRepository = saksdataRepository,
        )

        val file = exportService.getAsExcel(
            year = Year.now(),
            includeFritekst = true,
        )
        println(file.absolutePath)
    }

    @Test
    fun `generate excel file with no data does not throw exception`() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV1(
                any(),
                any(),
            )
        } returns getResultList(amount = 0)

        val exportServiceV1 = ExportServiceV1(
            saksdataRepository = saksdataRepository,
        )

        val file = exportServiceV1.getAsExcel(
            year = Year.now(),
            includeFritekst = true,
        )
        println(file.absolutePath)
    }

    @Test
    fun `generate huge excel file should not crash app`() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV1(
                any(),
                any(),
            )
        } returns getResultList(amount = 50_000)

        val exportService = ExportServiceV1(
            saksdataRepository = saksdataRepository,
        )

        val file = exportService.getAsExcel(
            year = Year.now(),
            includeFritekst = true,
        )
        println(file.absolutePath)
    }

    private fun getResultList(amount: Int = 1): List<Saksdata> {
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
                        kvalitetsvurderingReference = KvalitetsvurderingReference(
                            id = UUID.randomUUID(),
                            version = 1,
                        ),
                        kvalitetsvurderingV1 = KvalitetsvurderingV1()
                    ),
                )
            }
        }
    }
}