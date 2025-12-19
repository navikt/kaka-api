package no.nav.klage.kaka.clients.azure

import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logErrorResponse
import org.springframework.http.HttpStatusCode
import org.springframework.resilience.annotation.Retryable
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

        private const val userSelect =
            "onPremisesSamAccountName,displayName,givenName,surname,mail,officeLocation,userPrincipalName,id,jobTitle,streetAddress"

        private const val slimUserSelect = "userPrincipalName,onPremisesSamAccountName,displayName"
    }

    @Retryable
    fun getEnhetensAnsattesNavIdents(enhetNr: String): AzureSlimUserList? {
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
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(
                    response = response,
                    functionName = ::getEnhetensAnsattesNavIdents.name,
                    classLogger = logger,
                )
            }
            .bodyToMono<AzureSlimUserList>()
            .block()
    }

    @Retryable
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
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(
                    response = response,
                    functionName = ::getInnloggetSaksbehandler.name,
                    classLogger = logger,
                )
            }
            .bodyToMono<AzureUser>()
            .block()
            ?: throw RuntimeException("AzureAD data about authenticated user could not be fetched")
    }

    @Retryable
    fun getSaksbehandler(navIdent: String): AzureUser {
        logger.debug("Fetching data about authenticated user from Microsoft Graph")
        return findUserByNavIdent(navIdent)
    }

    @Retryable
    fun getInnloggetSaksbehandlersGroups(): List<AzureGroup> {
        logger.debug("Fetching data about authenticated users groups from Microsoft Graph")

        return microsoftGraphWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/me/memberOf")
                    .build()
            }.header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithGraphScope()}")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(
                    response = response,
                    functionName = ::getInnloggetSaksbehandlersGroups.name,
                    classLogger = logger,
                )
            }
            .bodyToMono<AzureGroupList>()
            .block()?.value
            ?: throw RuntimeException("AzureAD data about authenticated users groups could not be fetched")
    }

    @Retryable
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
        .onStatus(HttpStatusCode::isError) { response ->
            logErrorResponse(
                response = response,
                functionName = ::findUserByNavIdent.name,
                classLogger = logger,
            )
        }
        .bodyToMono<AzureUserList>().block()?.value?.firstOrNull()
        ?: throw RuntimeException("AzureAD data about user by nav ident $navIdent could not be fetched")
}