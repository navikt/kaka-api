package no.nav.klage.kaka.clients.azure

import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import no.nav.klage.kaka.util.logErrorResponse
import org.springframework.http.HttpStatusCode
import org.springframework.retry.annotation.Retryable
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
                logErrorResponse(response, ::getEnhetensAnsattesNavIdents.name, secureLogger)
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
                response.bodyToMono(String::class.java).map {
                    val errorString =
                        "Got ${response.statusCode()} when requesting ${::getInnloggetSaksbehandler.name} - response body: '$it'"
                    logger.error(errorString)
                    RuntimeException(errorString)
                }

                //logErrorResponse(response, ::getInnloggetSaksbehandler.name, logger)
            }
            .bodyToMono<AzureUser>()
            .block().let { secureLogger.debug("me: $it"); it }
            ?: throw RuntimeException("AzureAD data about authenticated user could not be fetched")
    }
}