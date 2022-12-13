package no.nav.klage.kaka.repositories

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.*
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KvalitetsvurderingV2RepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository

    @Test
    fun `add kvalitetsvurderingV2 works`() {
        val kvalitetsvurderingV2 = KvalitetsvurderingV2()
        kvalitetsvurderingV2Repository.save(kvalitetsvurderingV2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundKvalitetsvurderingV2 = kvalitetsvurderingV2Repository.getReferenceById(kvalitetsvurderingV2.id)
        assertThat(foundKvalitetsvurderingV2).isEqualTo(kvalitetsvurderingV2)
    }
}