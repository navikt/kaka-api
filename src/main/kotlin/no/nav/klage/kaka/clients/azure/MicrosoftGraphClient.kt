package no.nav.klage.kaka.clients.azure

import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class MicrosoftGraphClient(
    private val microsoftGraphWebClient: WebClient,
    private val tokenUtil: TokenUtil
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()

        private const val userSelect =
            "onPremisesSamAccountName,displayName,givenName,surname,mail,officeLocation,userPrincipalName,id,jobTitle,streetAddress"

        private const val slimUserSelect = "userPrincipalName,onPremisesSamAccountName,displayName"
    }

    fun getEnhetensAnsattesNavIdents(enhetNr: String): List<String> {
        return microsoftGraphWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/users")
                    .queryParam("\$filter", "streetAddress eq '$enhetNr'")
                    .queryParam("\$count", true)
                    .queryParam("\$top", 500)
                    .queryParam("\$select", slimUserSelect)
                    .build()
            }
            .header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithGraphScope()}")
            .header("ConsistencyLevel", "eventual")
            .retrieve()
            .bodyToMono<AzureSlimUserList>()
            .block()
            .let { userList -> userList?.value?.map { it.userPrincipalName } }
            ?: throw RuntimeException("AzureAD data about authenticated user could not be fetched")
    }

    //@Retryable
    fun getInnloggetSaksbehandler(): AzureUser {
        logger.debug("Fetching data about authenticated user from Microsoft Graph")

        return microsoftGraphWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/me")
                    .queryParam("\$select", userSelect)
                    .build()
            }.header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithGraphScope()}")

            .retrieve()
            .bodyToMono<AzureUser>()
            .block().let { secureLogger.debug("me: $it"); it }
            ?: throw RuntimeException("AzureAD data about authenticated user could not be fetched")
    }

    //@Retryable
    fun getSaksbehandler(navIdent: String): AzureUser {
        logger.debug("Fetching data about authenticated user from Microsoft Graph")
        return findUserByNavIdent(navIdent)
    }

    //@Retryable
    fun getInnloggetSaksbehandlersGroups(): List<AzureGroup> {
        logger.debug("Fetching data about authenticated users groups from Microsoft Graph")

        return microsoftGraphWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/me/memberOf")
                    .build()
            }.header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithGraphScope()}")
            .retrieve()
            .bodyToMono<AzureGroupList>()
            .block()?.value?.map { secureLogger.debug("AD Gruppe: $it"); it }
            ?: throw RuntimeException("AzureAD data about authenticated users groups could not be fetched")
    }

    //TODO: Denne har vi ikke brukt med OBO-token før, så det må testes
    private fun findUserByNavIdent(navIdent: String): AzureUser = microsoftGraphWebClient.get()
        .uri { uriBuilder ->
            uriBuilder
                .path("/users")
                .queryParam("\$filter", "mailnickname eq '$navIdent'")
                .queryParam("\$select", userSelect)
                .build()
        }
        .header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithGraphScope()}")
        .retrieve()
        .bodyToMono<AzureUserList>().block()?.value?.firstOrNull()?.let { secureLogger.debug("Saksbehandler: $it"); it }
        ?: throw RuntimeException("AzureAD data about user by nav ident $navIdent could not be fetched")
}