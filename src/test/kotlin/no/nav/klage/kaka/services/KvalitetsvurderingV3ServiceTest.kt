package no.nav.klage.kaka.services

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import no.nav.klage.kaka.repositories.KvalitetsvurderingV3Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.TokenUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.node.BooleanNode
import tools.jackson.databind.node.ObjectNode
import tools.jackson.databind.node.StringNode
import java.util.*

class KvalitetsvurderingV3ServiceTest {

    private val SAKSBEHANDLER_IDENT = "SAKSBEHANDLER_IDENT"

    private val kvalitetsvurderingV3Repository = mockk<KvalitetsvurderingV3Repository>()
    private val saksdataRepository = mockk<SaksdataRepository>()
    private val tokenUtil = mockk<TokenUtil>()
    private val jsonMapper = JsonMapper.builder().build()

    private lateinit var kvalitetsvurderingV3Service: KvalitetsvurderingV3Service

    private lateinit var kvalitetsvurdering: KvalitetsvurderingV3
    private lateinit var kvalitetsvurderingId: UUID

    @BeforeEach
    fun setUp() {
        kvalitetsvurderingV3Service = KvalitetsvurderingV3Service(
            kvalitetsvurderingV3Repository = kvalitetsvurderingV3Repository,
            saksdataRepository = saksdataRepository,
            tokenUtil = tokenUtil,
        )

        kvalitetsvurderingId = UUID.randomUUID()
        kvalitetsvurdering = KvalitetsvurderingV3(id = kvalitetsvurderingId)

        every { tokenUtil.getIdent() } returns SAKSBEHANDLER_IDENT
        every { kvalitetsvurderingV3Repository.getReferenceById(kvalitetsvurderingId) } returns kvalitetsvurdering
        every { saksdataRepository.findOneByKvalitetsvurderingReferenceId(kvalitetsvurderingId) } returns Saksdata(
            utfoerendeSaksbehandler = SAKSBEHANDLER_IDENT,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingId,
                version = 3,
            ),
        )
    }

    @Test
    fun `patchKvalitetsvurdering updates boolean field`() {
        val inputJson: ObjectNode = jsonMapper.createObjectNode()
        inputJson.set("saerregelverkAutomatiskVedtak", BooleanNode.TRUE)

        val result = kvalitetsvurderingV3Service.patchKvalitetsvurdering(kvalitetsvurderingId, inputJson)

        assertTrue(result.saerregelverkAutomatiskVedtak)
    }

    @Test
    fun `patchKvalitetsvurdering updates radiovalg field`() {
        val inputJson: ObjectNode = jsonMapper.createObjectNode()
        inputJson.set("saerregelverk", StringNode("BRA"))

        val result = kvalitetsvurderingV3Service.patchKvalitetsvurdering(kvalitetsvurderingId, inputJson)

        assertEquals(KvalitetsvurderingV3.Radiovalg.BRA, result.saerregelverk)
    }

    @Test
    fun `patchKvalitetsvurdering updates multiple fields`() {
        val inputJson: ObjectNode = jsonMapper.createObjectNode()
        inputJson.set("saerregelverkAutomatiskVedtak", BooleanNode.TRUE)
        inputJson.set("saerregelverkLovenErTolketEllerAnvendtFeil", BooleanNode.TRUE)
        inputJson.set("saerregelverk", StringNode("MANGELFULLT"))

        val result = kvalitetsvurderingV3Service.patchKvalitetsvurdering(kvalitetsvurderingId, inputJson)

        assertTrue(result.saerregelverkAutomatiskVedtak)
        assertTrue(result.saerregelverkLovenErTolketEllerAnvendtFeil)
        assertEquals(KvalitetsvurderingV3.Radiovalg.MANGELFULLT, result.saerregelverk)
    }

    @Test
    fun `patchKvalitetsvurdering updates saksbehandlingsregler boolean fields`() {
        val inputJson: ObjectNode = jsonMapper.createObjectNode()
        inputJson.set("saksbehandlingsreglerBruddPaaVeiledningsplikten", BooleanNode.TRUE)
        inputJson.set("saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser", BooleanNode.TRUE)

        val result = kvalitetsvurderingV3Service.patchKvalitetsvurdering(kvalitetsvurderingId, inputJson)

        assertTrue(result.saksbehandlingsreglerBruddPaaVeiledningsplikten)
        assertTrue(result.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser)
    }

    @Test
    fun `patchKvalitetsvurdering sets modified timestamp`() {
        val originalModified = kvalitetsvurdering.modified

        val inputJson: ObjectNode = jsonMapper.createObjectNode()
        inputJson.set("saerregelverkAutomatiskVedtak", BooleanNode.TRUE)

        val result = kvalitetsvurderingV3Service.patchKvalitetsvurdering(kvalitetsvurderingId, inputJson)

        assertNotNull(result.modified)
        assertNotEquals(originalModified, result.modified)
    }
}