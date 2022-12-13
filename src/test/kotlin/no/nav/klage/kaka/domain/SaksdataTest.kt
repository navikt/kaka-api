package no.nav.klage.kaka.domain

import no.nav.klage.kaka.domain.kodeverk.Role.ROLE_KAKA_KVALITETSTILBAKEMELDINGER
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.Ytelse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.*

internal class SaksdataTest {

    private val SAKEN_GJELDER = "15436621822"

//    @Test
//    fun `validation on empty saksdata gives correct number of errors`() {
//        val saksdata = Saksdata(
//            kvalitetsvurderingReference = KvalitetsvurderingReference(
//                id = UUID.randomUUID(),
//                version = 1,
//            ),
//            utfoerendeSaksbehandler = "SAKSBEHANDLER",
//            tilknyttetEnhet = "4295",
//        )
//
//        assertThrows<SectionedValidationErrorWithDetailsException> {
//            saksdata.validate()
//        }
//    }

    @Test
    fun `no validation of kvalitetsvurdering for TRUKKET`() {
        val saksdata = Saksdata(
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            ),
            utfoerendeSaksbehandler = "SAKSBEHANDLER",
            tilknyttetEnhet = "4295",
            mottattVedtaksinstans = LocalDate.now(),
            sakenGjelder = SAKEN_GJELDER,
            vedtaksinstansEnhet = "1",
            ytelse = Ytelse.OMS_OMP,
            mottattKlageinstans = LocalDate.now(),
            utfall = Utfall.TRUKKET,
        )

        saksdata.validateAndGetErrors()
    }

    @Test
    fun `same saksbehandler has read access`() {
        val utfoerendeSaksbehandler = "SAKSBEHANDLER"
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            ),
            sakenGjelder = SAKEN_GJELDER,
        )

        saksdata.verifyReadAccess(utfoerendeSaksbehandler)
    }

    @Test
    fun `other saksbehandler does not have read access`() {
        val utfoerendeSaksbehandler = "SAKSBEHANDLER"
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            ),
            sakenGjelder = SAKEN_GJELDER,
        )

        assertThrows<MissingTilgangException> {
            saksdata.verifyReadAccess("other")
        }
    }

    @Test
    fun `leder in vedtaksinstans has read access to his own enhet`() {
        val utfoerendeSaksbehandler = "SAKSBEHANDLER"
        val vedtaksinstansEnhet = "4000"
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = vedtaksinstansEnhet,
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            ),
            sakenGjelder = SAKEN_GJELDER,
        )

        saksdata.verifyReadAccess(
            innloggetIdent = "other",
            roller = setOf(ROLE_KAKA_KVALITETSTILBAKEMELDINGER),
            ansattEnhet = vedtaksinstansEnhet
        )
    }

    @Test
    fun `leder in vedtaksinstans does not have read access to other enhet`() {
        val utfoerendeSaksbehandler = "SAKSBEHANDLER"
        val saksdata = Saksdata(
            utfoerendeSaksbehandler = utfoerendeSaksbehandler,
            tilknyttetEnhet = "4295",
            vedtaksinstansEnhet = "4000",
            kvalitetsvurderingReference = KvalitetsvurderingReference(
                id = UUID.randomUUID(),
                version = 1,
            ),
            sakenGjelder = SAKEN_GJELDER,
        )

        assertThrows<MissingTilgangException> {
            saksdata.verifyReadAccess(
                innloggetIdent = "other",
                roller = setOf(ROLE_KAKA_KVALITETSTILBAKEMELDINGER),
                ansattEnhet = "5000"
            )
        }
    }
}