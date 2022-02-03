package no.nav.klage.kaka.clients.azure

import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerPersonligInfo
import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerRolle
import no.nav.klage.kaka.exceptions.EnhetNotFoundForSaksbehandlerException
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.klageenheter
import org.springframework.stereotype.Service

@Service
class AzureGateway(
    private val microsoftGraphClient: MicrosoftGraphClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    fun getAnsatteIEnhet(enhetNr: String): List<SaksbehandlerIdent> {
        return microsoftGraphClient.getEnhetensAnsattesNavIdents(enhetNr).map { SaksbehandlerIdent(it) }
    }

    fun getKlageenheterForSaksbehandler(ident: String): List<Enhet> =
        listOf(getPersonligDataOmSaksbehandlerMedIdent(ident).enhet).filter { it in klageenheter || it == Enhet.E4200 }
            .ifEmpty { throw EnhetNotFoundForSaksbehandlerException("$ident er ikke ansatt i en klageenhet") }

    fun getPersonligDataOmSaksbehandlerMedIdent(navIdent: String): SaksbehandlerPersonligInfo {
        val data = try {
            microsoftGraphClient.getSaksbehandler(navIdent)
        } catch (e: Exception) {
            logger.error("Failed to call getSaksbehandler for navident $navIdent", e)
            throw e
        }
        return SaksbehandlerPersonligInfo(
            data.onPremisesSamAccountName,
            data.id,
            data.givenName,
            data.surname,
            data.displayName,
            data.mail,
            mapToEnhet(data.streetAddress),
        )
    }

    fun getDataOmInnloggetSaksbehandler(): SaksbehandlerPersonligInfo {
        val data = try {
            microsoftGraphClient.getInnloggetSaksbehandler()
        } catch (e: Exception) {
            logger.error("Failed to call getInnloggetSaksbehandler", e)
            throw e
        }
        return SaksbehandlerPersonligInfo(
            data.onPremisesSamAccountName,
            data.id,
            data.givenName,
            data.surname,
            data.displayName,
            data.mail,
            mapToEnhet(data.streetAddress),
        )
    }

    fun getRollerForInnloggetSaksbehandler(): List<SaksbehandlerRolle> =
        try {
            microsoftGraphClient.getInnloggetSaksbehandlersGroups()
                .map { SaksbehandlerRolle(it.id, it.displayName ?: it.mailNickname ?: it.id) }
        } catch (e: Exception) {
            logger.error("Failed to call getInnloggetSaksbehandlersGroups", e)
            throw e
        }

    private fun mapToEnhet(enhetNr: String): Enhet =
        Enhet.values().find { it.navn == enhetNr }
            ?: throw EnhetNotFoundForSaksbehandlerException("Enhet ikke funnet med enhetNr $enhetNr")

}