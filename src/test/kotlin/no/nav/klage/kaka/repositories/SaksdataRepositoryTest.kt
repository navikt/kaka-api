package no.nav.klage.kaka.repositories


import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.RadioValg
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.RadioValgRaadgivendeLege
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.util.*

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SaksdataRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var saksdataRepository: SaksdataRepository

    @Autowired
    lateinit var kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository

    @Autowired
    lateinit var kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository

    @Test
    fun `add saksdata works`() {
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = "abc123",
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )

        saksdataRepository.save(saksdata)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundSaksdata = saksdataRepository.getReferenceById(saksdata.id)
        assertThat(foundSaksdata).isEqualTo(saksdata)
    }


    @Test
    fun `fullførte og pågående`() {
        val utfoerendeSaksbehandler = "abc123"
        val saksdataFullfoertx = Saksdata(
            utfoerendeSaksbehandler = "someoneelse",
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )
        val saksdataFullfoert1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            avsluttetAvSaksbehandler = LocalDateTime.now().minusDays(3),
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )
        val saksdataFullfoert2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            avsluttetAvSaksbehandler = LocalDateTime.now().minusDays(2),
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertx,
                saksdataFullfoert1,
                saksdataFullfoert2,
                saksdataPaagaaende1,
                saksdataPaagaaende2
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val fullfoerte =
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByModified(
                utfoerendeSaksbehandler, LocalDateTime.now().minusDays(7)
            )
        assertThat(fullfoerte).hasSize(2)

        val paagaaende =
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerIsNullOrderByCreated(
                utfoerendeSaksbehandler
            )
        assertThat(paagaaende).hasSize(2)
    }

    @Test
    fun `find one gives null result when no matching saksdata exists`() {
        val results = saksdataRepository.findOneByKvalitetsvurderingReferenceId(UUID.randomUUID())
        assertThat(results).isNull()
    }

    @Test
    fun `find one gives correct result`() {
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = "abc123",
            tilknyttetEnhet = "4295",
            ytelse = Ytelse.OMS_OMP,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            )
        )

        saksdataRepository.save(saksdata)

        testEntityManager.flush()
        testEntityManager.clear()

        val kvalitetsvurderingId = saksdata.kvalitetsvurderingReference.id

        val results = saksdataRepository.findOneByKvalitetsvurderingReferenceId(kvalitetsvurderingId)
        assertThat(results).isEqualTo(saksdata)
    }

    @Test
    fun `stats for finished based on dates works kvalitetsvurderingV1`() {
        val utfoerendeSaksbehandler = "abc123"
        val saksdataFullfoertx = Saksdata(
            utfoerendeSaksbehandler = "someoneelse",
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 12), LocalTime.NOON),
        )
        val saksdataFullfoert1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
        )
        val saksdataFullfoert2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertx,
                saksdataFullfoert1,
                saksdataFullfoert2,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val finished =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV1(
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
            )
        assertThat(finished).hasSize(2)
    }

    @Test
    fun `stats for finished based on dates works kvalitetsvurderingV2`() {
        val utfoerendeSaksbehandler = "abc123"
        val saksdataFullfoertx = Saksdata(
            utfoerendeSaksbehandler = "someoneelse",
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV2Repository.save(KvalitetsvurderingV2()).id,
                version = 2,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 12), LocalTime.NOON),
        )
        val saksdataFullfoert1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV2Repository.save(KvalitetsvurderingV2()).id,
                version = 2,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
        )
        val kvalitetsvurderingV2 = kvalitetsvurderingV2Repository.save(
            KvalitetsvurderingV2(
                vedtaketBruktFeilHjemmel = true,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = false,
                vedtaketBruktFeilHjemmelHjemlerList = setOf(Registreringshjemmel.ARBML_12, Registreringshjemmel.ARBML_17)
            )
        )
        val saksdataFullfoert2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV2.id,
                version = 2,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV2Repository.save(KvalitetsvurderingV2()).id,
                version = 2,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.MIN),
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV2Repository.save(KvalitetsvurderingV2()).id,
                version = 2,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 2), LocalTime.NOON),
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertx,
                saksdataFullfoert1,
                saksdataFullfoert2,
                saksdataPaagaaende1,
                saksdataPaagaaende2
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val finished =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV2(
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
            )
        assertThat(finished).hasSize(2)
    }

    @Test
    fun `use findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoert = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoert,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV1(
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )
        val saksdataFullfoertOtherVedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "4111",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.MIN),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1()).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 2), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
                saksdataFullfoertOtherVedtaksinstans,
                saksdataPaagaaende1,
                saksdataPaagaaende2
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = emptyList(),
                kommentarer = emptyList(),
            )
        assertThat(saksdata).hasSize(1)
    }


    @Test
    fun `saksdata for leder in foerste instans avvik kommentar`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        betydeligAvvikText = "et avvik"
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = emptyList(),
                kommentarer = listOf("avvik"),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans utredningen kommentar`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        utredningenAvAndreAktuelleForholdISakenText = "text"
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = emptyList(),
                kommentarer = listOf("utredningen"),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans opplaering kommentar`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        brukIOpplaeringText = "text"
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = emptyList(),
                kommentarer = listOf("opplaering"),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans forberedelsen and utredningen mangelfull`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdata1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        utredningenRadioValg = RadioValg.MANGELFULLT
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        val saksdata2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        klageforberedelsenRadioValg = RadioValg.MANGELFULLT,
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        val saksdata3NotRelated = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "1111",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        klageforberedelsenRadioValg = RadioValg.MANGELFULLT,
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = null,
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdata1,
                saksdata2,
                saksdata3NotRelated,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = listOf("forberedelsen", "utredningen"),
                kommentarer = emptyList(),
            )
        assertThat(saksdata).hasSize(2)
    }

    @Test
    fun `saksdata for leder in foerste instans forberedelsen mangelfull`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        klageforberedelsenRadioValg = RadioValg.MANGELFULLT,
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = listOf("forberedelsen"),
                kommentarer = emptyList(),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans utredningen mangelfull`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        utredningenRadioValg = RadioValg.MANGELFULLT,
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = listOf("utredningen"),
                kommentarer = emptyList(),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans rol mangelfull`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,

            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        brukAvRaadgivendeLegeRadioValg = RadioValgRaadgivendeLege.MANGELFULLT,
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = listOf("rol"),
                kommentarer = emptyList(),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans vedtaket mangelfull`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1(
                        vedtaketRadioValg = RadioValg.MANGELFULLT,
                    )
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdata =
            saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                mangelfullt = listOf("vedtaket"),
                kommentarer = emptyList(),
            )
        assertThat(saksdata).hasSize(1)
    }

    @Test
    fun `correct hits from db when getting stats for saksbehandler`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1()
                ).id,
                version = 1,
            ),
            registreringshjemler = setOf(
                Registreringshjemmel.AFP_62_OFFENTLIG,
                Registreringshjemmel.AFP_62_PRIVAT,
                Registreringshjemmel.ARBML_12
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
        )
        val saksdataFullfoertOtherVedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = "abs",
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "4111",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1()
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataFullfoertInSamevedtaksinstans,
                saksdataFullfoertOtherVedtaksinstans,
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val saksdataAvsluttet =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV1(
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
            )
        assertThat(saksdataAvsluttet).hasSize(2)
        assertThat(saksdataAvsluttet.map { it }).contains(saksdataFullfoertInSamevedtaksinstans)
    }


    @Test
    fun `findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val tilknyttetEnhet1 = "4295"
        val tilknyttetEnhet2 = "4293"

        val avsluttetAvSaksbehandler1 = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON)
        val avsluttetAvSaksbehandler2 = LocalDateTime.of(LocalDate.of(2022, Month.DECEMBER, 14), LocalTime.NOON)

        val saksdataTilknyttetEnhet1AvsluttetAvSaksbehandler1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = tilknyttetEnhet1,
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1()
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = avsluttetAvSaksbehandler1
        )

        val saksdataTilknyttetEnhet1AvsluttetAvSaksbehandler2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = tilknyttetEnhet1,
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1()
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = avsluttetAvSaksbehandler2
        )

        val saksdataTilknyttetEnhet2AvsluttetAvSaksbehandler1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = tilknyttetEnhet2,
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = kvalitetsvurderingV1Repository.save(
                    KvalitetsvurderingV1()
                ).id,
                version = 1,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = avsluttetAvSaksbehandler1
        )

        saksdataRepository.saveAll(
            mutableListOf(
                saksdataTilknyttetEnhet1AvsluttetAvSaksbehandler1,
                saksdataTilknyttetEnhet1AvsluttetAvSaksbehandler2,
                saksdataTilknyttetEnhet2AvsluttetAvSaksbehandler1
            )
        )

        testEntityManager.flush()
        testEntityManager.clear()

        val results =
            saksdataRepository.findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
                enhet = tilknyttetEnhet1,
                fromDateTime = avsluttetAvSaksbehandler1.minusMonths(1),
                toDateTime = avsluttetAvSaksbehandler1.plusMonths(1)

            )
        assertThat(results).hasSize(1)
        assertThat(results.first()).isEqualTo(saksdataTilknyttetEnhet1AvsluttetAvSaksbehandler1)
    }
}

