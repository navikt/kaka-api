package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.exceptions.SaksdataNotFoundException
import no.nav.klage.kaka.repositories.KvalitetsvurderingRepository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
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

    fun createSaksdata(innloggetSaksbehandler: String, tilknyttetEnhet: String): Saksdata {
        return saksdataRepository.save(
            Saksdata(
                utfoerendeSaksbehandler = innloggetSaksbehandler,
                tilknyttetEnhet = tilknyttetEnhet,
                kvalitetsvurdering = Kvalitetsvurdering()
            )
        )
    }

    fun createAndFinalizeSaksdata(
        sakenGjelder: String,
        sakstype: Type,
        ytelse: Ytelse,
        mottattVedtaksinstans: LocalDate,
        vedtaksinstansEnhet: String,
        mottattKlageinstans: LocalDate,
        utfall: Utfall,
        hjemler: List<Registreringshjemmel>,
        utfoerendeSaksbehandler: String,
        tilknyttetEnhet: String,
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
                registreringshjemler = hjemler.toSet(),
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
                utfoerendeSaksbehandler = utfoerendeSaksbehandler,
                tilknyttetEnhet = tilknyttetEnhet,
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

    fun setSakstype(saksdataId: UUID, sakstype: Type, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.sakstype = sakstype
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setYtelse(saksdataId: UUID, ytelse: Ytelse, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        if (saksdata.ytelse != ytelse) {
            setRegistreringshjemler(saksdataId, emptySet(), innloggetSaksbehandler)
        }
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

    fun setTilknyttetEnhet(saksdataId: UUID, enhetsId: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.tilknyttetEnhet = enhetsId
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

    fun setRegistreringshjemler(
        saksdataId: UUID,
        registreringshjemler: Set<Registreringshjemmel>,
        innloggetSaksbehandler: String
    ): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.registreringshjemler = registreringshjemler.toMutableSet()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setAvsluttetAvSaksbehandler(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.validate()
        if (saksdata.sakstype == Type.ANKE) {
            saksdata.mottattVedtaksinstans = null
            kvalitetsvurderingService.removeFieldsUnusedInAnke(saksdata.kvalitetsvurdering.id)
        }
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