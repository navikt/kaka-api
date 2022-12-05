package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.util.*


internal class ExportServiceV1Test {

    /* TODO fix
    @Test
    fun generateExcelFileAsLeder() {
        val saksdataRepository = mockk<SaksdataRepository>()
        val kvalitetsvurderingV1Repository = mockk<KvalitetsvurderingV1Repository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                any(),
                any()
            )
        } returns getSaksdata(amount = 10)

        val exportService = ExportService(
            saksdataRepository = saksdataRepository,
            kvalitetsvurderingV1Repository = kvalitetsvurderingV1Repository
        )

        val currDir = File(".")
        val path: String = currDir.absolutePath
        val fileLocation = path.substring(0, path.length - 1) + "testfile.xlsx"

        File(fileLocation).writeBytes(
            exportService.getAsExcel(
                year = Year.now()
            )
        )
    }*/

    @Test
    fun `generate excel file with no data does not throw exception`() {
        val saksdataRepository = mockk<SaksdataRepository>()
        val kvalitetsvurderingV1Repository = mockk<KvalitetsvurderingV1Repository>()

        every {
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                any(),
                any()
            )
        } returns getSaksdata(amount = 0)

        val exportServiceV1 = ExportServiceV1(
            saksdataRepository = saksdataRepository,
            kvalitetsvurderingV1Repository = kvalitetsvurderingV1Repository
        )

        val currDir = File(".")
        val path: String = currDir.absolutePath
        val fileLocation = path.substring(0, path.length - 1) + "testfile.xlsx"

        File(fileLocation).writeBytes(
            exportServiceV1.getAsExcel(
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
                        kvalitetsvurderingReference = KvalitetsvurderingReference(
                            id = UUID.randomUUID(),
                            version = 1,
                        ),
                    )
                )
            }
        }
    }
}