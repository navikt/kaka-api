package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
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

    val saksdataService =
        SaksdataService(
            saksdataRepository = saksdataRepository,
            kvalitetsvurderingV1Repository = mockk(),
            kvalitetsvurderingV2Repository = mockk(),
            kvalitetsvurderingV1Service = mockk(),
            kvalitetsvurderingV2Service = mockk(),
            azureGateway = mockk(),
            tokenUtil = mockk(),
            rolleMapper = mockk(),
            pdlFacade = mockk(),
            egenAnsattService = mockk(),
            kakaVersion2Date = mockk(),
            kakaVersion2_1Date = mockk(),
        )

    @BeforeEach
    fun beforeEach() {
        every { saksdataRepository.findById(any()) } returns Optional.of(
            Saksdata(
                utfoerendeSaksbehandler = SAKSBEHANDLER_IDENT,
                tilknyttetEnhet = "4295",
                kvalitetsvurderingReference = KvalitetsvurderingReference(
                    id = UUID.randomUUID(),
                    version = 1,
                ),
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