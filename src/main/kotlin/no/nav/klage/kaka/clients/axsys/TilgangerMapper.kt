package no.nav.klage.kaka.clients.axsys

import no.nav.klage.kaka.util.getLogger
import org.springframework.stereotype.Component
import no.nav.klage.kodeverk.Enhet as KodeverkEnhet

@Component
class TilgangerMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun mapTilgangerToEnheter(tilganger: Tilganger): List<KodeverkEnhet> =
        tilganger.enheter.mapNotNull { findKodeverkEnhet(it.enhetId) }

    fun findKodeverkEnhet(enhetsnr: String): KodeverkEnhet? =
        KodeverkEnhet.values().find { it.navn == enhetsnr } ?: logMissingEnhet(enhetsnr)

    fun logMissingEnhet(enhetsnr: String): no.nav.klage.kodeverk.Enhet? {
        logger.warn("Fant ikke Enhet med enhetsnr $enhetsnr")
        return null
    }
}


