package no.nav.klage.kaka.repositories

import io.mockk.mockk
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kodeverk.Source
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ActiveProfiles("local")
@DataJpaTest
class SaksdataResetIntegrationTest : PostgresIntegrationTestBase() {
    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var saksdataRepository: SaksdataRepository

    @Autowired
    lateinit var kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository

    @Autowired
    lateinit var kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository

    @Autowired
    lateinit var kvalitetsvurderingV3Repository: KvalitetsvurderingV3Repository

    @Test
    fun `reset current version clears persisted fields but retains its id and saksdata`() {
        val oldModified = LocalDateTime.of(2021, 2, 3, 4, 5)
        val hjemmel = Registreringshjemmel.values().first()
        val assessment =
            kvalitetsvurderingV3Repository.save(
                KvalitetsvurderingV3(
                    saerregelverkAutomatiskVedtak = true,
                    saerregelverk = KvalitetsvurderingV3.Radiovalg.MANGELFULLT,
                    saksbehandlingsreglerBruddPaaVeiledningsplikten = true,
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList =
                        setOf(hjemmel),
                    annetFritekst = "testinnhold",
                    modified = oldModified,
                ),
            )
        val saksdata =
            saksdataRepository.save(
                Saksdata(
                    sakenGjelder = "TEST_SAK",
                    utfoerendeSaksbehandler = "TEST_SAKSBEHANDLER",
                    tilknyttetEnhet = "TEST_ENHET",
                    source = Source.KAKA,
                    kvalitetsvurderingReference =
                        KvalitetsvurderingReference(
                            id = assessment.id,
                            version = 3,
                        ),
                    created = oldModified,
                    modified = oldModified,
                ),
            )

        saksdataService().resetKvalitetsvurdering(saksdataId = saksdata.id, innloggetSaksbehandler = saksdata.utfoerendeSaksbehandler)

        testEntityManager.flush()
        testEntityManager.clear()

        val persisted = saksdataRepository.findById(saksdata.id).orElseThrow()
        val resetAssessment =
            kvalitetsvurderingV3Repository
                .findById(persisted.kvalitetsvurderingReference.id)
                .orElseThrow()

        assertThat(persisted.kvalitetsvurderingReference.id).isEqualTo(assessment.id)
        assertThat(persisted.kvalitetsvurderingReference.version).isEqualTo(3)
        assertThat(resetAssessment.id).isEqualTo(assessment.id)
        assertThat(resetAssessment.saerregelverkAutomatiskVedtak).isFalse()
        assertThat(resetAssessment.saerregelverk).isNull()
        assertThat(resetAssessment.saksbehandlingsreglerBruddPaaVeiledningsplikten).isFalse()
        assertThat(
            resetAssessment.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList,
        ).isNullOrEmpty()
        assertThat(resetAssessment.annetFritekst).isNull()
        assertThat(persisted.sakenGjelder).isEqualTo(saksdata.sakenGjelder)
        assertThat(persisted.utfoerendeSaksbehandler).isEqualTo(saksdata.utfoerendeSaksbehandler)
        assertThat(persisted.tilknyttetEnhet).isEqualTo(saksdata.tilknyttetEnhet)
        assertThat(persisted.source).isEqualTo(saksdata.source)
        assertThat(persisted.created).isEqualTo(saksdata.created)
        assertThat(persisted.modified).isAfter(oldModified)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2])
    fun `reset previous version deletes it and stores an empty current version`(oldVersion: Int) {
        val oldAssessmentId = UUID.randomUUID()
        val oldModified = LocalDateTime.of(2021, 2, 3, 4, 5)
        when (oldVersion) {
            1 -> {
                kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        id = oldAssessmentId,
                        sakensDokumenter = true,
                        utredningenAvMedisinskeForholdText = "testinnhold",
                    ),
                )
            }

            2 -> {
                kvalitetsvurderingV2Repository.save(
                    KvalitetsvurderingV2(
                        id = oldAssessmentId,
                        klageforberedelsenSakensDokumenter = true,
                    ),
                )
            }
        }
        val saksdata =
            saksdataRepository.save(
                Saksdata(
                    sakenGjelder = "TEST_SAK",
                    utfoerendeSaksbehandler = "TEST_SAKSBEHANDLER",
                    tilknyttetEnhet = "TEST_ENHET",
                    kvalitetsvurderingReference =
                        KvalitetsvurderingReference(
                            id = oldAssessmentId,
                            version = oldVersion,
                        ),
                    created = oldModified.withNano(123456789),
                    modified = oldModified,
                ),
            )

        testEntityManager.flush()
        testEntityManager.clear()
        val originalCreated = saksdataRepository.findById(saksdata.id).orElseThrow().created

        saksdataService().resetKvalitetsvurderingForSystem(saksdata.id)

        testEntityManager.flush()
        testEntityManager.clear()

        val persisted = saksdataRepository.findById(saksdata.id).orElseThrow()
        val newAssessmentId = persisted.kvalitetsvurderingReference.id
        val newAssessment =
            kvalitetsvurderingV3Repository
                .findById(newAssessmentId)
                .orElseThrow()

        assertThat(newAssessmentId).isNotEqualTo(oldAssessmentId)
        assertThat(persisted.kvalitetsvurderingReference.version).isEqualTo(3)
        assertThat(newAssessment.saerregelverkAutomatiskVedtak).isFalse()
        assertThat(newAssessment.saerregelverk).isNull()
        assertThat(newAssessment.saksbehandlingsreglerBruddPaaVeiledningsplikten).isFalse()
        assertThat(
            newAssessment.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList,
        ).isNullOrEmpty()
        assertThat(newAssessment.annetFritekst).isNull()
        assertThat(persisted.sakenGjelder).isEqualTo(saksdata.sakenGjelder)
        assertThat(persisted.utfoerendeSaksbehandler).isEqualTo(saksdata.utfoerendeSaksbehandler)
        assertThat(persisted.tilknyttetEnhet).isEqualTo(saksdata.tilknyttetEnhet)
        assertThat(persisted.source).isEqualTo(saksdata.source)
        assertThat(persisted.created).isEqualTo(originalCreated)
        assertThat(persisted.modified).isAfter(oldModified)

        when (oldVersion) {
            1 -> assertThat(kvalitetsvurderingV1Repository.existsById(oldAssessmentId)).isFalse()
            2 -> assertThat(kvalitetsvurderingV2Repository.existsById(oldAssessmentId)).isFalse()
        }
    }

    private fun saksdataService() =
        SaksdataService(
            saksdataRepository = saksdataRepository,
            kvalitetsvurderingV1Repository = kvalitetsvurderingV1Repository,
            kvalitetsvurderingV2Repository = kvalitetsvurderingV2Repository,
            kvalitetsvurderingV3Repository = kvalitetsvurderingV3Repository,
            kvalitetsvurderingV1Service = mockk(),
            kvalitetsvurderingV2Service = mockk(),
            kvalitetsvurderingV3Service = mockk(),
            tokenUtil = mockk(),
            rolleMapper = mockk(),
            kakaVersion2Date = LocalDate.now().minusYears(1),
            kakaVersion3Date = LocalDate.now(),
            klageLookupClient = mockk(),
            saksbehandlerService = mockk(),
        )
}
