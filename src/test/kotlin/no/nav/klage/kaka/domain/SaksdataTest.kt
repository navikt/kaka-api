package no.nav.klage.kaka.domain

import no.nav.klage.kaka.exceptions.ValidationErrorWithDetailsException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SaksdataTest {

    @Test
    fun `validation on empty saksdata gives correct number of errors`() {
        val saksdata = Saksdata(
            kvalitetsvurdering = Kvalitetsvurdering(),
            utfoerendeSaksbehandler = "SAKSBEHANDLER"
        )

        assertThrows<ValidationErrorWithDetailsException> {
            saksdata.validate()
        }
    }
}