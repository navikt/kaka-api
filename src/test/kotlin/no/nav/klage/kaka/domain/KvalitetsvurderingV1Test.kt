package no.nav.klage.kaka.domain

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.RadioValg
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.RadioValgRaadgivendeLege
import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.ytelse.Ytelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class KvalitetsvurderingV1Test {

    @Test
    fun cleanup() {
        val kvalitetsvurderingV1 = KvalitetsvurderingV1(
            klageforberedelsenRadioValg = RadioValg.BRA,
            sakensDokumenter = true,
            konklusjonen = true,
            utredningenRadioValg = RadioValg.MANGELFULLT,
            utredningenAvAndreAktuelleForholdISaken = false,
            utredningenAvAndreAktuelleForholdISakenText = "TEXT",
            arbeidsrettetBrukeroppfoelging = true,
            arbeidsrettetBrukeroppfoelgingText = "TEXT",
            vedtaketRadioValg = RadioValg.MANGELFULLT,
            detErFeilIKonkretRettsanvendelse = true,
            rettsregelenErBenyttetFeil = false
        )

        kvalitetsvurderingV1.cleanup()

        assertFalse(kvalitetsvurderingV1.sakensDokumenter)
        assertFalse(kvalitetsvurderingV1.konklusjonen)
        assertFalse(kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISaken)
        assertNull(kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISakenText)
        assertTrue(kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelging)
        assertEquals(kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelgingText, "TEXT")
        assertTrue(kvalitetsvurderingV1.detErFeilIKonkretRettsanvendelse)
        assertFalse(kvalitetsvurderingV1.rettsregelenErBenyttetFeil)
    }

    @Test
    fun `validation on empty kvalitetsvurdering for klage gives correct number of errors`() {
        val kvalitetsvurderingV1 = KvalitetsvurderingV1()
        val results = kvalitetsvurderingV1.getInvalidProperties(null, Type.KLAGE)
        assertThat(results).hasSize(3)
    }

    @Test
    fun `validation on empty kvalitetsvurdering for anke gives correct number of errors`() {
        val kvalitetsvurderingV1 = KvalitetsvurderingV1()
        val results = kvalitetsvurderingV1.getInvalidProperties(null, Type.ANKE)
        assertThat(results).hasSize(2)
    }

    @Test
    fun `validation on partly filled kvalitetsvurdering gives correct number of errors`() {
        val kvalitetsvurderingV1 = KvalitetsvurderingV1(
            klageforberedelsenRadioValg = RadioValg.BRA,
            vedtaketRadioValg = RadioValg.MANGELFULLT
        )
        val results = kvalitetsvurderingV1.getInvalidProperties(null, Type.KLAGE)
        assertThat(results).hasSize(2)
    }

    @Test
    fun `validation on partly filled kvalitetsvurdering requiring raadgivende lege gives correct number of errors`() {
        val kvalitetsvurderingV1 = KvalitetsvurderingV1(
            klageforberedelsenRadioValg = RadioValg.BRA,
            vedtaketRadioValg = RadioValg.MANGELFULLT
        )
        val results = kvalitetsvurderingV1.getInvalidProperties(Ytelse.GRU_GRU, Type.KLAGE)
        assertThat(results).hasSize(3)
        assertThat(results).contains(
            InvalidProperty(
                field = KvalitetsvurderingV1::brukAvRaadgivendeLegeRadioValg.name,
                reason = "Velg et alternativ."
            )
        )
    }

    @Test
    fun `validation on partly filled brukAvRaadgivendeLege gives correct number of errors`() {
        val kvalitetsvurderingV1 = KvalitetsvurderingV1(
            klageforberedelsenRadioValg = RadioValg.BRA,
            vedtaketRadioValg = RadioValg.MANGELFULLT,
            brukAvRaadgivendeLegeRadioValg = RadioValgRaadgivendeLege.MANGELFULLT
        )
        val results = kvalitetsvurderingV1.getInvalidProperties(Ytelse.GRU_GRU, Type.KLAGE)
        assertThat(results).hasSize(3)
        assertThat(results).contains(
            InvalidProperty(
                field = KvalitetsvurderingV1::brukAvRaadgivendeLegeRadioValg.name,
                reason = "Velg minst én."
            )
        )
    }
}