package no.nav.klage.kaka.services

import no.nav.klage.kaka.clients.ereg.EregClient
import no.nav.klage.kaka.clients.pdl.PdlFacade
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getTeamLogger
import no.nav.klage.kaka.util.isValidFnrOrDnr
import no.nav.klage.kaka.util.isValidOrgnr
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.ytelseToRegistreringshjemlerV2
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.Month

@Service
class AdminService(
    private val saksdataRepository: SaksdataRepository,
    private val pdlFacade: PdlFacade,
    private val eregClient: EregClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val teamLogger = getTeamLogger()
    }

    fun logInvalidSakenGjelder() {
        val results = saksdataRepository.findAll()
        var errorsFound = 0
        var errorString = ""
        teamLogger.debug("Getting all invalid registered sakenGjelder values.")
        teamLogger.debug("Size: " + results.size)
        results.forEach {
            if (it.avsluttetAvSaksbehandler != null) {
                if (it.sakenGjelder?.length == 11) {
                    if (!isValidFnrOrDnr(it.sakenGjelder!!)) {
                        errorString += "Invalid fnr ${it.sakenGjelder}, saksdata id ${it.id}, saksbehandler ${it.utfoerendeSaksbehandler} - "
                        errorsFound++
                    } else if (!pdlFacade.personExists(it.sakenGjelder!!)) {
                        errorString += "Fnr not found in pdl ${it.sakenGjelder}, saksdata id ${it.id}, saksbehandler ${it.utfoerendeSaksbehandler} - "
                        errorsFound++
                    }

                } else if (it.sakenGjelder?.length == 9) {
                    if (!isValidOrgnr(it.sakenGjelder!!)) {
                        errorString += "Invalid orgnr ${it.sakenGjelder}, saksdata id ${it.id}, saksbehandler ${it.utfoerendeSaksbehandler} - "
                        errorsFound++
                    } else if (!eregClient.organisasjonExists(it.sakenGjelder!!)) {
                        errorString += "Orgnr not found in ereg ${it.sakenGjelder}, saksdata id ${it.id}, saksbehandler ${it.utfoerendeSaksbehandler} - "
                        errorsFound++
                    }
                } else {
                    errorString += "Invalid sakenGjelder nr ${it.sakenGjelder}, saksdata id ${it.id}, saksbehandler ${it.utfoerendeSaksbehandler} - "
                    errorsFound++
                }
            }
        }
        teamLogger.debug("Errors found: $errorString")
        teamLogger.debug("Number of invalid values found: $errorsFound")
    }

    fun logV1HjemlerInV2() {
        val results = saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV2(
            fromDateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0),
            toDateTime = LocalDateTime.now()
        )

        var resultString = "logV1HjemlerInV2:\n\n"

        results.forEach { (saksdata, _) ->
            saksdata.registreringshjemler?.forEach { hjemmel ->
                if (hjemmel !in ytelseToRegistreringshjemlerV2[saksdata.ytelse]!!) {
                    resultString += "Hjemmel with id ${hjemmel.id} in saksdata ${saksdata.id} invalid\n"
                }
            }
        }

        logger.debug(resultString)
    }

    @Transactional
    fun migrateTilbakekreving() {
        val candidates = saksdataRepository.findByTilbakekrevingIsFalse()
        logger.debug("Found ${candidates.size} candidates for tilbakekreving migration.")
        var migrations = 0
        candidates.forEach { candidate ->
            if (candidate.registreringshjemler?.any {
                    it in tilbakekrevingHjemler
                } == true
            ) {
                candidate.tilbakekreving = true
                migrations++
            }
        }
        logger.debug("Migrated $migrations candidates.")
    }

    val tilbakekrevingHjemler = listOf(
        Registreringshjemmel.FTRL_22_15_TILBAKEKREVING,
        Registreringshjemmel.FTRL_22_15A,
        Registreringshjemmel.FTRL_22_15B,
        Registreringshjemmel.FTRL_22_15C,
        Registreringshjemmel.FTRL_22_15G,
        Registreringshjemmel.FTRL_22_15D,
        Registreringshjemmel.FTRL_22_15E,
        Registreringshjemmel.FTRL_22_15F,
        Registreringshjemmel.FORSKL_8,
        Registreringshjemmel.INNKL_25_T,
        Registreringshjemmel.INNKL_26A_T,
        Registreringshjemmel.INNKL_26B_T,
        Registreringshjemmel.INNKL_29,
        Registreringshjemmel.FTRL_22_17A,
        Registreringshjemmel.FTRL_4_28,
        Registreringshjemmel.SUP_ST_L_13,
        Registreringshjemmel.BTRL_13,
        Registreringshjemmel.KONTSL_11,
    )
}