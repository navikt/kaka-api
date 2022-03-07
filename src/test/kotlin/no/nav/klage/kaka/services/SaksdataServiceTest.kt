package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.clients.axsys.AxsysGateway
import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.InvalidSakenGjelderException
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class SaksdataServiceTest {

    private val SAKSBEHANDLER_IDENT = "SAKSBEHANDLER_IDENT"
    private val OTHER_IDENT = "OTHER_IDENT"

    val saksdataRepository = mockk<SaksdataRepository>()
    val kvalitetsvurderingService = mockk<KvalitetsvurderingService>()
    val axsysGateway = mockk<AxsysGateway>()

    val saksdataService =
        SaksdataService(
            saksdataRepository,
            mockk(),
            kvalitetsvurderingService,
            axsysGateway,
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
                kvalitetsvurdering = Kvalitetsvurdering()
            )
        )
    }


    @Test
    fun `throws exception if not allowed to delete`() {
        assertThrows<MissingTilgangException> {
            saksdataService.deleteSaksdata(saksdataId = UUID.randomUUID(), innloggetSaksbehandler = OTHER_IDENT)
        }
    }

    @Nested
    inner class SakenGjelder {
        @Test
        fun `incorrect fnr`() {
            assertThrows<InvalidSakenGjelderException> {
                saksdataService.setSakenGjelder(
                    saksdataId = UUID.randomUUID(),
                    sakenGjelder = "16436621822",
                    innloggetSaksbehandler = SAKSBEHANDLER_IDENT
                )
            }
        }

        @Test
        fun `incorrect orgnr`() {
            assertThrows<InvalidSakenGjelderException> {
                saksdataService.setSakenGjelder(
                    saksdataId = UUID.randomUUID(),
                    sakenGjelder = "974652289",
                    innloggetSaksbehandler = SAKSBEHANDLER_IDENT
                )
            }
        }

        @Test
        fun `correct fnr`() {
            saksdataService.setSakenGjelder(
                saksdataId = UUID.randomUUID(),
                sakenGjelder = "15436621822",
                innloggetSaksbehandler = SAKSBEHANDLER_IDENT
            )
        }

        @Test
        fun `correct orgnr`() {
            saksdataService.setSakenGjelder(
                saksdataId = UUID.randomUUID(),
                sakenGjelder = "974652269",
                innloggetSaksbehandler = SAKSBEHANDLER_IDENT
            )
        }

        @Test
        fun `incorrect input`() {
            assertThrows<InvalidSakenGjelderException> {
                saksdataService.setSakenGjelder(
                    saksdataId = UUID.randomUUID(),
                    sakenGjelder = SAKSBEHANDLER_IDENT,
                    innloggetSaksbehandler = SAKSBEHANDLER_IDENT
                )
            }
        }
    }


}