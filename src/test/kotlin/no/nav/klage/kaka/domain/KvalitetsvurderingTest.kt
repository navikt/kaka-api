package no.nav.klage.kaka.domain

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
}