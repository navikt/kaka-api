package no.nav.klage.kaka.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IdentUtilTest {
    @Test
    fun `ugyldig fødselsnummer gir feil`() {
        assertThat(isValidFnrOrDnr("12345678910")).isFalse
    }

    @Test
    fun `gyldig d-nummer gir rett svar`() {
        assertThat(isValidFnrOrDnr("02446701749")).isTrue
    }

    @Test
    fun `ugyldig orgnr gir feil`() {
        assertThat(isValidOrgnr("974652279")).isFalse
    }

    @Test
    fun `gyldig orgnr gir rett svar`() {
        assertThat(isValidOrgnr("974652269")).isTrue
    }
}