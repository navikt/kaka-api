package no.nav.klage.kaka.util

import no.nav.klage.auth.TexasClient
import no.nav.klage.auth.callingApp
import no.nav.klage.auth.currentAzureToken
import no.nav.klage.auth.displayName
import no.nav.klage.auth.groups
import no.nav.klage.auth.navIdent
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val texasClient: TexasClient,
    @Value($$"api://${NAIS_CLUSTER_NAME}.${NAIS_NAMESPACE}.${KLAGE_LOOKUP_APP_NAME}/.default")
    private val klageLookupScope: String,
) {
    fun getIdent(): String = currentAzureToken()?.navIdent ?: throw RuntimeException("Ident not found in token")

    fun getName(): String = currentAzureToken()?.displayName ?: throw RuntimeException("name not found in token")

    /** azp_name has the form `dev-gcp:some-team:some-consumer`. */
    fun getCallingApplication(): String = currentAzureToken()?.callingApp.orEmpty()

    fun getGroups(): List<String> = currentAzureToken()?.groups.orEmpty()

    fun getOnBehalfOfTokenWithKlageLookupScope(): String = texasClient.onBehalfOfCurrentUser(klageLookupScope)
}
