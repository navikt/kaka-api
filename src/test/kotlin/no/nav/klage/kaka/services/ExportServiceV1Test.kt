package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.api.view.ExcelQueryParams
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.repositories.SaksdataRepositoryCustomImpl
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Source
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.ytelse.Ytelse
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


internal class ExportServiceV1Test {

    @Test
    fun generateExcelFileAsLeder() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByQueryParamsV1(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns getResultList(amount = 10)

        val exportService = ExportServiceV1(
            saksdataRepository = saksdataRepository,
        )

        val file = exportService.getAsExcel(
            includeFritekst = true,
            queryParams = ExcelQueryParams(
                fromDate = LocalDate.now(),
                toDate = LocalDate.now(),
                tilbakekreving = "false",
                klageenheter = emptyList(),
                vedtaksinstansgrupper = emptyList(),
                enheter = emptyList(),
                types = emptyList(),
                ytelser = emptyList(),
                utfall = emptyList(),
                hjemler = emptyList(),
            )
        )
        println(file.absolutePath)
    }

    @Test
    fun `generate excel file with no data does not throw exception`() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByQueryParamsV1(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns getResultList(amount = 0)

        val exportServiceV1 = ExportServiceV1(
            saksdataRepository = saksdataRepository,
        )

        val file = exportServiceV1.getAsExcel(
            includeFritekst = true,
            queryParams = ExcelQueryParams(
                fromDate = LocalDate.now(),
                toDate = LocalDate.now(),
                tilbakekreving = "false",
                klageenheter = emptyList(),
                vedtaksinstansgrupper = emptyList(),
                enheter = emptyList(),
                types = emptyList(),
                ytelser = emptyList(),
                utfall = emptyList(),
                hjemler = emptyList(),
            )
        )
        println(file.absolutePath)
    }

    @Test
    fun `generate huge excel file should not crash app`() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every {
            saksdataRepository.findByQueryParamsV1(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns getResultList(amount = 50_000)

        val exportService = ExportServiceV1(
            saksdataRepository = saksdataRepository,
        )

        val file = exportService.getAsExcel(
            includeFritekst = true,
            queryParams = ExcelQueryParams(
                fromDate = LocalDate.now(),
                toDate = LocalDate.now(),
                tilbakekreving = "false",
                klageenheter = emptyList(),
                vedtaksinstansgrupper = emptyList(),
                enheter = emptyList(),
                types = emptyList(),
                ytelser = emptyList(),
                utfall = emptyList(),
                hjemler = emptyList(),
            )
        )
        println(file.absolutePath)
    }

    private fun getResultList(amount: Int = 1): Set<SaksdataRepositoryCustomImpl.QueryResultV1> {
        return buildSet {
            repeat(amount) {
                add(
                    SaksdataRepositoryCustomImpl.QueryResultV1(
                        saksdata = Saksdata(
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
                        ),
                        kvalitetsvurdering = KvalitetsvurderingV1()
                    )
                )
            }
        }
    }
}