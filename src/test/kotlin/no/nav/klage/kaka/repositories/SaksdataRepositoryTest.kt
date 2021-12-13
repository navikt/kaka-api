package no.nav.klage.kaka.repositories


import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kodeverk.Ytelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
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
    fun `fullførte og pågående`() {
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
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByCreated(
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

}
