package no.nav.klage.kaka.services

import no.nav.klage.kaka.clients.ereg.EregClient
import no.nav.klage.kaka.clients.pdl.PdlFacade
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import no.nav.klage.kaka.util.isValidFnrOrDnr
import no.nav.klage.kaka.util.isValidOrgnr
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val saksdataRepository: SaksdataRepository,
    private val pdlFacade: PdlFacade,
    private val eregClient: EregClient,
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
        results.forEach{
            if (it.sakenGjelder?.length == 11) {
                if (!isValidFnrOrDnr(it.sakenGjelder!!)) {
                    secureLogger.debug("Invalid fnr ${it.sakenGjelder}, saksdata id ${it.id}")
                } else if (!pdlFacade.personExists(it.sakenGjelder!!)) {
                    secureLogger.debug("Fnr not found in pdl ${it.sakenGjelder}, saksdata id ${it.id}")
                }

            } else if (it.sakenGjelder?.length == 9) {
                if (!isValidOrgnr(it.sakenGjelder!!)){
                    secureLogger.debug("Invalid orgnr ${it.sakenGjelder}, saksdata id ${it.id}")
                } else if (eregClient.organisasjonExists(it.sakenGjelder!!)) {
                    secureLogger.debug("Orgnr not found in ereg ${it.sakenGjelder}, saksdata id ${it.id}")
                }
            } else {
                secureLogger.debug("Invalid sakenGjelder nr ${it.sakenGjelder}, saksdata id ${it.id}")
            }
        }
    }
}