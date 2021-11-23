package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.*
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.exceptions.SaksdataNotFoundException
import no.nav.klage.kaka.repositories.KvalitetsvurderingRepository
import no.nav.klage.kaka.repositories.SaksdataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class SaksdataService(
    private val saksdataRepository: SaksdataRepository,
    private val kvalitetsvurderingRepository: KvalitetsvurderingRepository,
    private val kvalitetsvurderingService: KvalitetsvurderingService
) {
    fun getSaksdata(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        return getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
    }

    fun createSaksdata(innloggetSaksbehandler: String): Saksdata {
        return saksdataRepository.save(
            Saksdata(
                utfoerendeSaksbehandler = innloggetSaksbehandler,
                kvalitetsvurdering = Kvalitetsvurdering()
            )
        )
    }

    fun createAndFinalizeSaksdata(
        sakenGjelder: String,
        sakstype: Sakstype,
        ytelse: Ytelse,
        mottattVedtaksinstans: LocalDate,
        vedtaksinstansEnhet: String,
        mottattKlageinstans: LocalDate,
        utfall: Utfall,
        hjemler: List<Hjemmel>,
        utfoerendeSaksbehandler: String,
        kvalitetsvurderingId: UUID,
        avsluttetAvSaksbehandler: LocalDateTime,
    ): Saksdata {
        //TODO: Her skal det skje en validering før noen oppdatering skjer.
        kvalitetsvurderingService.cleanUpKvalitetsvurdering(kvalitetsvurderingId)
        return saksdataRepository.save(
            Saksdata(
                sakenGjelder = sakenGjelder,
                sakstype = sakstype,
                ytelse = ytelse,
                mottattKlageinstans = mottattKlageinstans,
                vedtaksinstansEnhet = vedtaksinstansEnhet,
                mottattVedtaksinstans = mottattVedtaksinstans,
                utfall = utfall,
                hjemler = hjemler.toSet(),
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
                utfoerendeSaksbehandler = utfoerendeSaksbehandler,
                kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId)
            )
        )
    }

    fun setSakenGjelder(saksdataId: UUID, sakenGjelder: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdata.sakenGjelder = sakenGjelder
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setSakstype(saksdataId: UUID, sakstype: Sakstype, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.sakstype = sakstype
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setYtelse(saksdataId: UUID, ytelse: Ytelse, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.ytelse = ytelse
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattVedtaksinstans(saksdataId: UUID, dato: LocalDate, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.mottattVedtaksinstans = dato
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setVedtaksinstansEnhet(saksdataId: UUID, enhetsId: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.vedtaksinstansEnhet = enhetsId
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattKlageinstans(saksdataId: UUID, dato: LocalDate, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.mottattKlageinstans = dato
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setUtfall(saksdataId: UUID, utfall: Utfall, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.utfall = utfall
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setHjemler(saksdataId: UUID, hjemler: Set<Hjemmel>, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.hjemler = hjemler.toMutableSet()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setAvsluttetAvSaksbehandler(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.validate()
        kvalitetsvurderingService.cleanUpKvalitetsvurdering(saksdata.kvalitetsvurdering.id)
        saksdata.avsluttetAvSaksbehandler = LocalDateTime.now()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    private fun getSaksdataAndVerifyAccess(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = saksdataRepository.findById(saksdataId)
        if (saksdata.isEmpty) {
            throw SaksdataNotFoundException("Could not find saksdata with id $saksdataId")
        }
        return saksdata.get().also {
            it.verifyAccess(innloggetSaksbehandler)
        }
    }

    private fun getSaksdataAndVerifyAccessForEdit(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = saksdataRepository.findById(saksdataId)
        if (saksdata.isEmpty) {
            throw SaksdataNotFoundException("Could not find saksdata with id $saksdataId")
        }
        return saksdata.get().also {
            it.verifyAccess(innloggetSaksbehandler)
            if (it.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException("Saksdataen er allerede fullført")
        }
    }

    fun search(saksbehandlerIdent: String, fullfoert: Boolean, daysSince: Int?): List<Saksdata> {
        return if (fullfoert) {
            val dateFrom = LocalDate.now().atStartOfDay().minusDays(daysSince?.toLong() ?: 7)
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByCreated(
                saksbehandlerIdent,
                dateFrom
            )
        } else {
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerIsNullOrderByCreated(
                saksbehandlerIdent
            )
        }
    }

    fun deleteSaksdata(saksdataId: UUID, innloggetSaksbehandler: String) {
        getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
        saksdataRepository.deleteById(saksdataId)
    }
}