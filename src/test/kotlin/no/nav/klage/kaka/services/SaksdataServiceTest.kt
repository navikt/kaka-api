package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.exceptions.SaksdataNotFoundException
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.KvalitetsvurderingV2Repository
import no.nav.klage.kaka.repositories.KvalitetsvurderingV3Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

internal class SaksdataServiceTest {
    private val saksbehandlerIdent = "SAKSBEHANDLER_IDENT"
    private val otherIdent = "OTHER_IDENT"

    val saksdataRepository = mockk<SaksdataRepository>()
    private val kvalitetsvurderingV1Repository = mockk<KvalitetsvurderingV1Repository>()
    private val kvalitetsvurderingV2Repository = mockk<KvalitetsvurderingV2Repository>()
    private val kvalitetsvurderingV3Repository = mockk<KvalitetsvurderingV3Repository>()

    val saksdataService =
        SaksdataService(
            saksdataRepository = saksdataRepository,
            kvalitetsvurderingV1Repository = kvalitetsvurderingV1Repository,
            kvalitetsvurderingV2Repository = kvalitetsvurderingV2Repository,
            kvalitetsvurderingV1Service = mockk(),
            kvalitetsvurderingV2Service = mockk(),
            tokenUtil = mockk(),
            rolleMapper = mockk(),
            kakaVersion2Date = LocalDate.now().minusYears(1),
            kvalitetsvurderingV3Repository = kvalitetsvurderingV3Repository,
            kvalitetsvurderingV3Service = mockk(),
            kakaVersion3Date = LocalDate.now(),
            klageLookupClient = mockk(),
            saksbehandlerService = mockk(),
        )

    @BeforeEach
    fun beforeEach() {
        every { saksdataRepository.findById(any()) } returns
            Optional.of(
                Saksdata(
                    utfoerendeSaksbehandler = saksbehandlerIdent,
                    tilknyttetEnhet = "4295",
                    kvalitetsvurderingReference =
                        KvalitetsvurderingReference(
                            id = UUID.randomUUID(),
                            version = 1,
                        ),
                ),
            )
    }

    @Test
    fun `both reset entry points reject finalized saksdata without repository writes`() {
        val finalizedSaksdata =
            Saksdata(
                utfoerendeSaksbehandler = saksbehandlerIdent,
                tilknyttetEnhet = "4295",
                avsluttetAvSaksbehandler = LocalDateTime.now(),
                kvalitetsvurderingReference = KvalitetsvurderingReference(id = UUID.randomUUID(), version = 1),
            )
        every { saksdataRepository.findById(any()) } returns Optional.of(finalizedSaksdata)

        assertThrows<SaksdataFinalizedException> {
            saksdataService.resetKvalitetsvurdering(saksdataId = finalizedSaksdata.id, innloggetSaksbehandler = saksbehandlerIdent)
        }
        assertThrows<SaksdataFinalizedException> {
            saksdataService.resetKvalitetsvurderingForSystem(finalizedSaksdata.id)
        }

        verifyNoResetWrites()
    }

    @Test
    fun `both reset entry points reject missing saksdata`() {
        every { saksdataRepository.findById(any()) } returns Optional.empty()
        val missingId = UUID.randomUUID()

        assertThrows<SaksdataNotFoundException> {
            saksdataService.resetKvalitetsvurdering(saksdataId = missingId, innloggetSaksbehandler = saksbehandlerIdent)
        }
        assertThrows<SaksdataNotFoundException> {
            saksdataService.resetKvalitetsvurderingForSystem(missingId)
        }

        verifyNoResetWrites()
    }

    @Test
    fun `reset rejects another saksbehandler without repository writes`() {
        assertThrows<MissingTilgangException> {
            saksdataService.resetKvalitetsvurdering(saksdataId = UUID.randomUUID(), innloggetSaksbehandler = otherIdent)
        }

        verifyNoResetWrites()
    }

    @Test
    fun `throws exception if not allowed to delete`() {
        assertThrows<MissingTilgangException> {
            saksdataService.deleteSaksdata(saksdataId = UUID.randomUUID(), innloggetSaksbehandler = otherIdent)
        }
    }

    private fun verifyNoResetWrites() {
        verify(exactly = 0) { saksdataRepository.save(any()) }
        verify(exactly = 0) { kvalitetsvurderingV1Repository.save(any()) }
        verify(exactly = 0) { kvalitetsvurderingV1Repository.deleteById(any()) }
        verify(exactly = 0) { kvalitetsvurderingV2Repository.save(any()) }
        verify(exactly = 0) { kvalitetsvurderingV2Repository.deleteById(any()) }
        verify(exactly = 0) { kvalitetsvurderingV3Repository.save(any()) }
        verify(exactly = 0) { kvalitetsvurderingV3Repository.deleteById(any()) }
    }
}
