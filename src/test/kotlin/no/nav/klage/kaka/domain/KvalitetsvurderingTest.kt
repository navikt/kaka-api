package no.nav.klage.kaka.domain

import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Ytelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
    fun `validation on empty kvalitetsvurdering for klage gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering()
        val results = kvalitetsvurdering.getInvalidProperties(null, Type.KLAGE)
        assertThat(results).hasSize(3)
    }

    @Test
    fun `validation on empty kvalitetsvurdering for anke gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering()
        val results = kvalitetsvurdering.getInvalidProperties(null, Type.ANKE)
        assertThat(results).hasSize(2)
    }

    @Test
    fun `validation on partly filled kvalitetsvurdering gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering(
            klageforberedelsenRadioValg = Kvalitetsvurdering.RadioValg.BRA,
            vedtaketRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT
        )
        val results = kvalitetsvurdering.getInvalidProperties(null, null)
        assertThat(results).hasSize(2)
    }

    @Test
    fun `validation on partly filled kvalitetsvurdering requiring raadgivende lege gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering(
            klageforberedelsenRadioValg = Kvalitetsvurdering.RadioValg.BRA,
            vedtaketRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT
        )
        val results = kvalitetsvurdering.getInvalidProperties(Ytelse.GRU_GRU, Type.KLAGE)
        assertThat(results).hasSize(3)
        assertThat(results).contains(
            InvalidProperty(
                field = Kvalitetsvurdering::brukAvRaadgivendeLegeRadioValg.name,
                reason = "Velg et alternativ."
            )
        )
    }

    @Test
    fun `validation on partly filled brukAvRaadgivendeLege gives correct number of errors`() {
        val kvalitetsvurdering = Kvalitetsvurdering(
            klageforberedelsenRadioValg = Kvalitetsvurdering.RadioValg.BRA,
            vedtaketRadioValg = Kvalitetsvurdering.RadioValg.MANGELFULLT,
            brukAvRaadgivendeLegeRadioValg = Kvalitetsvurdering.RadioValgRaadgivendeLege.MANGELFULLT
        )
        val results = kvalitetsvurdering.getInvalidProperties(Ytelse.GRU_GRU, Type.KLAGE)
        assertThat(results).hasSize(3)
        assertThat(results).contains(
            InvalidProperty(
                field = Kvalitetsvurdering::brukAvRaadgivendeLegeRadioValg.name,
                reason = "Velg minst én."
            )
        )
    }
}