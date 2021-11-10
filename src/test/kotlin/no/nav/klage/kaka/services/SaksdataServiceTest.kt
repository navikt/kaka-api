package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class SaksdataServiceTest {

    @Test
    fun `throws exception if not allowed to delete`() {
        val saksdataRepository = mockk<SaksdataRepository>()

        every { saksdataRepository.getById(any()) } returns Saksdata(
            utfoerendeSaksbehandler = "abc123",
            kvalitetsvurdering = Kvalitetsvurdering(
                utfoerendeSaksbehandler = "abc123"
            )
        )

        val saksdataService = SaksdataService(saksdataRepository)

        assertThrows<MissingTilgangException> {
            saksdataService.deleteSaksdata(saksdataId = UUID.randomUUID(), innloggetSaksbehandler = "otherIdent")
        }
    }

}