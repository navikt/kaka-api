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
        return microsoftGraphClient.getEnhetensAnsattesNavIdents(enhetNr)?.value?.map {
            SaksbehandlerIdent(
                navIdent = it.onPremisesSamAccountName,
                displayName = it.displayName,
            )
        } ?: emptyList()
    }

    fun getStreetAddressInnloggetSaksbehandler(): String {
        val data = try {
            microsoftGraphClient.getInnloggetSaksbehandler()
        } catch (e: Exception) {
            logger.error("Error in ${::getStreetAddressInnloggetSaksbehandler.name}, failed to call getInnloggetSaksbehandler", e)
            throw e
        }
        return data.streetAddress
    }

    fun getDataOmInnloggetSaksbehandler(): SaksbehandlerPersonligInfo {
        val data = try {
            microsoftGraphClient.getInnloggetSaksbehandler()
        } catch (e: Exception) {
            logger.error("Error in ${::getDataOmInnloggetSaksbehandler.name}, failed to call getInnloggetSaksbehandler", e)
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

    private fun mapToEnhet(enhetNr: String): Enhet =
        Enhet.entries.find { it.navn == enhetNr }
            ?: throw EnhetNotFoundForSaksbehandlerException("Enhet ikke funnet med enhetNr $enhetNr")
}