package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class SaksdataServiceTest {

    private val SAKSBEHANDLER_IDENT = "SAKSBEHANDLER_IDENT"
    private val OTHER_IDENT = "OTHER_IDENT"

    val saksdataRepository = mockk<SaksdataRepository>()
    val kvalitetsvurderingService = mockk<KvalitetsvurderingService>()

    val saksdataService =
        SaksdataService(
            saksdataRepository,
            mockk(),
            kvalitetsvurderingService,
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk()
        )

    @BeforeEach
    fun beforeEach() {
        every { saksdataRepository.findById(any()) } returns Optional.of(
            Saksdata(
                utfoerendeSaksbehandler = SAKSBEHANDLER_IDENT,
                tilknyttetEnhet = "4295",
                kvalitetsvurderingV1 = KvalitetsvurderingV1()
            )
        )
    }


    @Test
    fun `throws exception if not allowed to delete`() {
        assertThrows<MissingTilgangException> {
            saksdataService.deleteSaksdata(saksdataId = UUID.randomUUID(), innloggetSaksbehandler = OTHER_IDENT)
        }
    }
}