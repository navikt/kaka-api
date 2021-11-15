package no.nav.klage.kaka.domain

import no.nav.klage.kaka.domain.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class KvalitetsvurderingTest {

    @Test
    fun cleanup() {
        val kvalitetsvurdering = Kvalitetsvurdering(
            klageforberedelsenRadioValg = Kvalitetsvurdering.RadioValg.BRA,
            sakensDokumenter = true,
            konklusjonen = true,
            utredningenRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT,
            utredningenAvAndreAktuelleForholdISaken = false,
            utredningenAvAndreAktuelleForholdISakenText = "TEXT",
            arbeidsrettetBrukeroppfoelging = true,
            arbeidsrettetBrukeroppfoelgingText = "TEXT",
            vedtaketRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT,
            detErFeilIKonkretRettsanvendelse = true,
            rettsregelenErBenyttetFeil = false
        )

        kvalitetsvurdering.cleanup()

        assertFalse(kvalitetsvurdering.sakensDokumenter)
        assertFalse(kvalitetsvurdering.konklusjonen)
        assertFalse(kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken)
        assertNull(kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText)
        assertTrue(kvalitetsvurdering.arbeidsrettetBrukeroppfoelging)
        assertEquals(kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText, "TEXT")
        assertTrue(kvalitetsvurdering.detErFeilIKonkretRettsanvendelse)
        assertFalse(kvalitetsvurdering.rettsregelenErBenyttetFeil)
    }

    @Test
    fun `validation on empty kvalitetsvurdering gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering()
        val results = kvalitetsvurdering.getInvalidProperties(Tema.DAG)
        assertThat(results).hasSize(3)
    }

    @Test
    fun `validation on partly filled kvalitetsvurdering gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering(
            klageforberedelsenRadioValg = Kvalitetsvurdering.RadioValg.BRA,
            vedtaketRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT
        )
        val results = kvalitetsvurdering.getInvalidProperties(Tema.DAG)
        assertThat(results).hasSize(1)
    }

    @Test
    fun `validation on partly filled kvalitetsvurdering requiring raadgivende lege gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering(
            klageforberedelsenRadioValg = Kvalitetsvurdering.RadioValg.BRA,
            vedtaketRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT
        )
        val results = kvalitetsvurdering.getInvalidProperties(Tema.SYK)
        assertThat(results).hasSize(2)
    }
}
