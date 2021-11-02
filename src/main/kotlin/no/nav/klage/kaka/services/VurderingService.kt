package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.Vurdering
import no.nav.klage.kaka.domain.kodeverk.Hjemmel
import no.nav.klage.kaka.domain.kodeverk.Sakstype
import no.nav.klage.kaka.domain.kodeverk.Tema
import no.nav.klage.kaka.domain.kodeverk.Utfall
import no.nav.klage.kaka.repositories.VurderingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class VurderingService(
    private val vurderingRepository: VurderingRepository
) {
    fun getVurdering(vurderingId: UUID, innloggetSaksbehandler: String): Optional<Vurdering> {
        return vurderingRepository.findById(vurderingId)
    }

    fun getAllVurdering(): MutableList<Vurdering> {
        return vurderingRepository.findAll()
    }

    fun createVurdering(innloggetSaksbehandler: String): Vurdering {
        return vurderingRepository.save(
            Vurdering(
                utfoerendeSaksbehandler = innloggetSaksbehandler,
                kvalitetsvurdering = Kvalitetsvurdering(
                    utfoerendeSaksbehandler = innloggetSaksbehandler
                )
            )
        )
    }

    fun setKlager(vurderingId: UUID, klager: String, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.klager = klager
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setSakstype(vurderingId: UUID, sakstype: Sakstype, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.sakstype = sakstype
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setTema(vurderingId: UUID, tema: Tema, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.tema = tema
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setMottattVedtaksinstans(vurderingId: UUID, dato: LocalDate, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.mottattVedtaksinstans = dato
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setVedtaksinstansEnhet(vurderingId: UUID, enhetsId: String, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.vedtaksinstansEnhet = enhetsId
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setMottattKlageinstans(vurderingId: UUID, dato: LocalDate, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.mottattKlageinstans = dato
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setUtfall(vurderingId: UUID, utfall: Utfall, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.utfall = utfall
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setHjemler(vurderingId: UUID, hjemler: Set<Hjemmel>, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.hjemler = hjemler.toMutableSet()
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    fun setAvsluttetAvSaksbehandler(vurderingId: UUID, innloggetSaksbehandler: String): Vurdering {
        val vurdering = getVurderingAndVerifyAccess(vurderingId, innloggetSaksbehandler)
        vurdering.avsluttetAvSaksbehandler = LocalDateTime.now()
        vurdering.modified = LocalDateTime.now()
        return vurdering
    }

    private fun getVurderingAndVerifyAccess(vurderingId: UUID, innloggetSaksbehandler: String): Vurdering {
        return vurderingRepository.getById(vurderingId).also { it.verifyAccess(innloggetSaksbehandler) }
    }
}