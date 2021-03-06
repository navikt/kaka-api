package no.nav.klage.kaka.repositories


import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.RadioValg
import no.nav.klage.kaka.domain.kodeverk.RadioValgRaadgivendeLege
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
    lateinit var kvalitetsvurderingRepository: KvalitetsvurderingRepository

    @Test
    fun `add saksdata works`() {
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = "abc123",
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering()
        )

        saksdataRepository.save(saksdata)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundSaksdata = saksdataRepository.getById(saksdata.id)
        assertThat(foundSaksdata).isEqualTo(saksdata)
    }

    @Test
    fun `fullf??rte og p??g??ende`() {
        val utfoerendeSaksbehandler = "abc123"
        val saksdataFullfoertx = Saksdata(
            utfoerendeSaksbehandler = "someoneelse",
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering()
        )
        val saksdataFullfoert1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            avsluttetAvSaksbehandler = LocalDateTime.now().minusDays(3),
            kvalitetsvurdering = Kvalitetsvurdering()
        )
        val saksdataFullfoert2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            avsluttetAvSaksbehandler = LocalDateTime.now().minusDays(2),
            kvalitetsvurdering = Kvalitetsvurdering()
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering()
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering()
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
    fun `delete saksdata (and belonging vurdering) works`() {
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = "abc123",
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering()
        )

        saksdataRepository.save(saksdata)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundSaksdata = saksdataRepository.findById(saksdata.id)
        assertThat(foundSaksdata.get()).isEqualTo(saksdata)

        assertThat(kvalitetsvurderingRepository.findAll()).hasSize(1)

        saksdataRepository.deleteById(saksdata.id)

        testEntityManager.flush()
        testEntityManager.clear()

        foundSaksdata = saksdataRepository.findById(saksdata.id)
        assertThat(foundSaksdata).isEmpty
        assertThat(kvalitetsvurderingRepository.findAll()).isEmpty()
    }

    @Test
    fun `find one gives null result when no matching saksdata exists`() {
        val results = saksdataRepository.findOneByKvalitetsvurderingId(UUID.randomUUID())
        assertThat(results).isNull()
    }

    @Test
    fun `find one gives correct result`() {
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = "abc123",
            tilknyttetEnhet = "4295",
            ytelse = Ytelse.OMS_OMP,
            kvalitetsvurdering = Kvalitetsvurdering()
        )

        saksdataRepository.save(saksdata)

        testEntityManager.flush()
        testEntityManager.clear()

        val kvalitetsvurderingId = saksdata.kvalitetsvurdering.id

        val results = saksdataRepository.findOneByKvalitetsvurderingId(kvalitetsvurderingId)
        assertThat(results).isEqualTo(saksdata)
    }

    @Test
    fun `stats for finished and unfinished based on dates works`() {
        val utfoerendeSaksbehandler = "abc123"
        val saksdataFullfoertx = Saksdata(
            utfoerendeSaksbehandler = "someoneelse",
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 12), LocalTime.NOON),
        )
        val saksdataFullfoert1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
        )
        val saksdataFullfoert2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.MIN),
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurdering = Kvalitetsvurdering(),
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
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
            )
        assertThat(finished).hasSize(2)

        val unfinished =
            saksdataRepository.findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.MIN),
            )
        assertThat(unfinished).hasSize(1)
    }

    @Test
    fun `saksdata for leder in foerste instans`() {
        val utfoerendeSaksbehandler = "abc123"
        val vedtaksinstansEnhet = "4020"
        val saksdataFullfoertInSamevedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )
        val saksdataFullfoertOtherVedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "4111",
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.MIN),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4),
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurdering = Kvalitetsvurdering(),
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                betydeligAvvikText = "et avvik"
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                utredningenAvAndreAktuelleForholdISakenText = "text"
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                brukIOpplaeringText = "text"
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                utredningenRadioValg = RadioValg.MANGELFULLT
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        val saksdata2 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurdering = Kvalitetsvurdering(
                klageforberedelsenRadioValg = RadioValg.MANGELFULLT,
            ),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
            registreringshjemler = setOf(Registreringshjemmel.FTRL_9_4)
        )

        val saksdata3NotRelated = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "1111",
            kvalitetsvurdering = Kvalitetsvurdering(
                klageforberedelsenRadioValg = RadioValg.MANGELFULLT,
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                klageforberedelsenRadioValg = RadioValg.MANGELFULLT
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                utredningenRadioValg = RadioValg.MANGELFULLT
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                brukAvRaadgivendeLegeRadioValg = RadioValgRaadgivendeLege.MANGELFULLT
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(
                vedtaketRadioValg = RadioValg.MANGELFULLT
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
            saksdataRepository.findForVedtaksinstansleder(
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
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 14), LocalTime.NOON),
        )
        val saksdataFullfoertOtherVedtaksinstans = Saksdata(
            utfoerendeSaksbehandler = "abs",
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "4111",
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.NOON),
            avsluttetAvSaksbehandler = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 13), LocalTime.MIN),
        )
        val saksdataPaagaaende1 = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 3), LocalTime.MIN),
        )
        val saksdataPaagaaende2 = Saksdata(
            utfoerendeSaksbehandler = "zxc",
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurdering = Kvalitetsvurdering(),
            created = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 2), LocalTime.NOON),
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

        val saksdataAvsluttet =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
                fromDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 1), LocalTime.MIN),
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                saksbehandler = utfoerendeSaksbehandler,
            )
        assertThat(saksdataAvsluttet).hasSize(1)
        assertThat(saksdataAvsluttet.first()).isEqualTo(saksdataFullfoertInSamevedtaksinstans)

        val saksdataPaagaaende =
            saksdataRepository.findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerOrderByCreated(
                toDateTime = LocalDateTime.of(LocalDate.of(2022, Month.JANUARY, 15), LocalTime.MIN),
                saksbehandler = utfoerendeSaksbehandler,
            )
        assertThat(saksdataPaagaaende).hasSize(1)
        assertThat(saksdataPaagaaende.first()).isEqualTo(saksdataPaagaaende1)
    }

}