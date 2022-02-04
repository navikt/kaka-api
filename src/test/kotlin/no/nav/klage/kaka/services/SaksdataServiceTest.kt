package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.clients.axsys.AxsysGateway
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
        val kvalitetsvurderingService = mockk<KvalitetsvurderingService>()
        val axsysGateway = mockk<AxsysGateway>()

        every { saksdataRepository.findById(any()) } returns Optional.of(
            Saksdata(
                utfoerendeSaksbehandler = "abc123",
                tilknyttetEnhet = "4295",
                kvalitetsvurdering = Kvalitetsvurdering()
            )
        )

        val saksdataService =
            SaksdataService(saksdataRepository, mockk(), kvalitetsvurderingService, axsysGateway, mockk(), mockk())

        assertThrows<MissingTilgangException> {
            saksdataService.deleteSaksdata(saksdataId = UUID.randomUUID(), innloggetSaksbehandler = "otherIdent")
        }
    }
}