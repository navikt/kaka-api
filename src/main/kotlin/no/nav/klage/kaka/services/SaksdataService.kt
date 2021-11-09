package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.Hjemmel
import no.nav.klage.kaka.domain.kodeverk.Sakstype
import no.nav.klage.kaka.domain.kodeverk.Tema
import no.nav.klage.kaka.domain.kodeverk.Utfall
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class SaksdataService(
    private val saksdataRepository: SaksdataRepository
) {
    fun getSaksdata(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        return saksdataRepository.getById(saksdataId)
    }

    fun createSaksdata(innloggetSaksbehandler: String): Saksdata {
        return saksdataRepository.save(
            Saksdata(
                utfoerendeSaksbehandler = innloggetSaksbehandler,
                kvalitetsvurdering = Kvalitetsvurdering(
                    utfoerendeSaksbehandler = innloggetSaksbehandler
                )
            )
        )
    }

    fun setKlager(saksdataId: UUID, klager: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.klager = klager
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setSakstype(saksdataId: UUID, sakstype: Sakstype, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.sakstype = sakstype
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setTema(saksdataId: UUID, tema: Tema, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.tema = tema
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattVedtaksinstans(saksdataId: UUID, dato: LocalDate, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.mottattVedtaksinstans = dato
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setVedtaksinstansEnhet(saksdataId: UUID, enhetsId: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.vedtaksinstansEnhet = enhetsId
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattKlageinstans(saksdataId: UUID, dato: LocalDate, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.mottattKlageinstans = dato
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setUtfall(saksdataId: UUID, utfall: Utfall, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.utfall = utfall
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setHjemler(saksdataId: UUID, hjemler: Set<Hjemmel>, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.hjemler = hjemler.toMutableSet()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setAvsluttetAvSaksbehandler(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.avsluttetAvSaksbehandler = LocalDateTime.now()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    private fun getSaksdataAndVerifyAccess(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        return saksdataRepository.getById(saksdataId).also { it.verifyAccess(innloggetSaksbehandler) }
    }
}