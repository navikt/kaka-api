package no.nav.klage.kaka.services

import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val saksdataRepository: SaksdataRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun logCorruptData() {
        val results = saksdataRepository.findAll()
        secureLogger.debug("Testing, testing")
        secureLogger.debug("Size: " +results.size)
        secureLogger.debug("First: " +results[0])
    }
}