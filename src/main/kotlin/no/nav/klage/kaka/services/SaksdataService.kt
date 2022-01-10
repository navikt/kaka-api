package no.nav.klage.kaka.services

import no.nav.klage.kaka.clients.axsys.AxsysGateway
import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.exceptions.SaksdataNotFoundException
import no.nav.klage.kaka.repositories.KvalitetsvurderingRepository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.Source
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
    private val kvalitetsvurderingService: KvalitetsvurderingService,
    private val axsysGateway: AxsysGateway,
) {
    fun getSaksdata(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        return getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
    }

    fun createSaksdata(innloggetSaksbehandler: String, tilknyttetEnhet: String?): Saksdata {
        return saksdataRepository.save(
            Saksdata(
                utfoerendeSaksbehandler = innloggetSaksbehandler,
                tilknyttetEnhet = tilknyttetEnhet
                    ?: axsysGateway.getKlageenheterForSaksbehandler(innloggetSaksbehandler).first().navn,
                kvalitetsvurdering = Kvalitetsvurdering()
            )
        )
    }

    fun handleIncomingCompleteSaksdata(
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
        source: Source
    ): Saksdata {
        kvalitetsvurderingService.cleanUpKvalitetsvurdering(kvalitetsvurderingId)

        val existingSaksdata = saksdataRepository.findOneByKvalitetsvurderingId(kvalitetsvurderingId)
        return if (existingSaksdata != null) {
            existingSaksdata.sakenGjelder = sakenGjelder
            existingSaksdata.sakstype = sakstype
            existingSaksdata.ytelse = ytelse
            existingSaksdata.mottattVedtaksinstans = mottattVedtaksinstans
            existingSaksdata.vedtaksinstansEnhet = vedtaksinstansEnhet
            existingSaksdata.mottattKlageinstans = mottattKlageinstans
            existingSaksdata.utfall = utfall
            existingSaksdata.registreringshjemler = hjemler.toSet()
            existingSaksdata.utfoerendeSaksbehandler = utfoerendeSaksbehandler
            existingSaksdata.tilknyttetEnhet = tilknyttetEnhet
            existingSaksdata.avsluttetAvSaksbehandler = avsluttetAvSaksbehandler
            existingSaksdata.source = source
            existingSaksdata.modified = LocalDateTime.now()

            existingSaksdata
        } else {
            saksdataRepository.save(
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
                    kvalitetsvurdering = kvalitetsvurderingRepository.getById(kvalitetsvurderingId),
                    source = source
                )
            )
        }
    }

    fun setSakenGjelder(saksdataId: UUID, sakenGjelder: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
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

    fun setYtelse(saksdataId: UUID, ytelse: Ytelse?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        if (saksdata.ytelse != ytelse) {
            setRegistreringshjemler(saksdataId, emptySet(), innloggetSaksbehandler)
        }
        saksdata.ytelse = ytelse
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattVedtaksinstans(saksdataId: UUID, dato: LocalDate?, innloggetSaksbehandler: String): Saksdata {
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
        if (saksdata.tilknyttetEnhet != enhetsId) {
            setYtelse(saksdataId, null, innloggetSaksbehandler)
        }
        saksdata.tilknyttetEnhet = enhetsId
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattKlageinstans(saksdataId: UUID, dato: LocalDate?, innloggetSaksbehandler: String): Saksdata {
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

    fun reopenSaksdata(saksdataId: UUID, innloggetSaksbehandler: String) {
        val saksdata = getSaksdataAndVerifyAccessForEdit(
            saksdataId = saksdataId,
            innloggetSaksbehandler = innloggetSaksbehandler,
            isReopen = true
        )
        saksdata.avsluttetAvSaksbehandler = null
        saksdata.modified = LocalDateTime.now()
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

    private fun getSaksdataAndVerifyAccessForEdit(
        saksdataId: UUID,
        innloggetSaksbehandler: String,
        isReopen: Boolean = false
    ): Saksdata {
        val saksdata = saksdataRepository.findById(saksdataId)
        if (saksdata.isEmpty) {
            throw SaksdataNotFoundException("Could not find saksdata with id $saksdataId")
        }
        return saksdata.get().also {
            it.verifyAccess(innloggetSaksbehandler)
            if (!isReopen && it.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException("Saksdataen er allerede fullført")
        }
    }

    fun search(saksbehandlerIdent: String, fullfoert: Boolean, daysSince: Int?): List<Saksdata> {
        return if (fullfoert) {
            val dateFrom = LocalDate.now().atStartOfDay().minusDays(daysSince?.toLong() ?: 7)
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByModified(
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
        getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdataRepository.deleteById(saksdataId)
    }
}