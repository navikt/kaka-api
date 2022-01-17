package no.nav.klage.kaka.domain

import no.nav.klage.kaka.exceptions.SectionedValidationErrorWithDetailsException
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.Ytelse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class SaksdataTest {

    @Test
    fun `validation on empty saksdata gives correct number of errors`() {
        val saksdata = Saksdata(
            kvalitetsvurdering = Kvalitetsvurdering(),
            utfoerendeSaksbehandler = "SAKSBEHANDLER",
            tilknyttetEnhet = "4295",
        )

        assertThrows<SectionedValidationErrorWithDetailsException> {
            saksdata.validate()
        }
    }

    @Test
    fun `no validation of kvalitetsvurdering for TRUKKET`() {
        val saksdata = Saksdata(
            kvalitetsvurdering = Kvalitetsvurdering(),
            utfoerendeSaksbehandler = "SAKSBEHANDLER",
            tilknyttetEnhet = "4295",
            mottattVedtaksinstans = LocalDate.now(),
            sakenGjelder = "12345678910",
            vedtaksinstansEnhet = "1",
            ytelse = Ytelse.OMS_OMP,
            mottattKlageinstans = LocalDate.now(),
            utfall = Utfall.TRUKKET,
        )

        saksdata.validate()
    }
}