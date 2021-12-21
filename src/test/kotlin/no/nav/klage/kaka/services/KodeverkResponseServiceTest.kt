package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.toDto
import no.nav.klage.kodeverk.Enhet
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("local")
@SpringBootTest(classes = [KodeverkResponseService::class])
internal class KodeverkResponseServiceTest {

    @Autowired
    lateinit var kodeverkResponseService: KodeverkResponseService

    @Test
    fun `kodeverk ytelse does not contain Vikafossen when not specified`() {
        val kodeverkResponse = kodeverkResponseService.getKodeVerkResponse()
        assertThat(kodeverkResponse.ytelser).noneSatisfy {
            assertThat(it.enheter).contains(Enhet.E2103.toDto())
            assertThat(it.klageenheter).contains(Enhet.E2103.toDto())
        }
    }

    @Test
    fun `kodeverk ytelse contains Vikafossen when specified`() {
        val kodeverkResponse = kodeverkResponseService.getKodeVerkResponse(true)
        assertThat(kodeverkResponse.ytelser).allSatisfy {
            assertThat(it.enheter).contains(Enhet.E2103.toDto())
            assertThat(it.klageenheter).contains(Enhet.E2103.toDto())
        }
    }
}