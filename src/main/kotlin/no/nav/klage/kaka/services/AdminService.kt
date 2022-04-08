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

    fun logInvalidSakenGjelder() {
        val results = saksdataRepository.findAll()
        var errorsFound = 0
        var errorString = ""
        secureLogger.debug("Getting all invalid registered sakenGjelder values.")
        secureLogger.debug("Size: " +results.size)
        results.forEach{
            if (it.avsluttetAvSaksbehandler != null) {
                if (it.sakenGjelder?.length == 11) {
                    if (!isValidFnrOrDnr(it.sakenGjelder!!)) {
                        errorString += "Invalid fnr ${it.sakenGjelder}, saksdata id ${it.id}, - "
                        errorsFound++
                    } else if (!pdlFacade.personExists(it.sakenGjelder!!)) {
                        errorString += "Fnr not found in pdl ${it.sakenGjelder}, saksdata id ${it.id}, - "
                        errorsFound++
                    }

                } else if (it.sakenGjelder?.length == 9) {
                    if (!isValidOrgnr(it.sakenGjelder!!)) {
                        errorString += "Invalid orgnr ${it.sakenGjelder}, saksdata id ${it.id}, - "
                        errorsFound++
                    } else if (eregClient.organisasjonExists(it.sakenGjelder!!)) {
                        errorString += "Orgnr not found in ereg ${it.sakenGjelder}, saksdata id ${it.id}, - "
                        errorsFound++
                    }
                } else {
                    errorString += "Invalid sakenGjelder nr ${it.sakenGjelder}, saksdata id ${it.id}, - "
                    errorsFound++
                }
            }
        }
        secureLogger.debug("Errors found: $errorString")
        secureLogger.debug("Number of invalid values found: $errorsFound")
    }
}