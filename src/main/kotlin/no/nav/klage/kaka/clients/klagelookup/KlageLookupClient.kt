package no.nav.klage.kaka.clients.klagelookup

import no.nav.klage.kaka.exceptions.EnhetNotFoundException
import no.nav.klage.kaka.exceptions.UserNotFoundException
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono


@Component
class KlageLookupClient(
    private val klageLookupWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Retryable
    fun getAccess(
        /** fnr, dnr or aktorId */
        brukerId: String,
        navIdent: String,
    ): Access {
        return runWithTimingAndLogging {
            val token = "Bearer ${tokenUtil.getOnBehalfOfTokenWithKlageLookupScope()}"

            val accessRequest = AccessRequest(
                brukerId = brukerId,
                navIdent = navIdent,
            )

            klageLookupWebClient.post()
                .uri("/access-to-person")
                .bodyValue(accessRequest)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    token,
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    logErrorResponse(
                        response = response,
                        functionName = ::getAccess.name,
                        classLogger = logger,
                    )
                }
                .bodyToMono<Access>()
                .block() ?: throw RuntimeException("Could not get access")
        }
    }

    @Retryable(
        excludes = [UserNotFoundException::class]
    )
    fun getUserInfo(
        navIdent: String,
    ): ExtendedUserResponse {
        return runWithTimingAndLogging {
            val token = "Bearer ${tokenUtil.getOnBehalfOfTokenWithKlageLookupScope()}"
            klageLookupWebClient.get()
                .uri("/users/$navIdent")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    token,
                )
                .exchangeToMono { response ->
                    if (response.statusCode().value() == 404) {
                        logger.debug("User $navIdent not found")
                        Mono.error(UserNotFoundException("User $navIdent not found"))
                    } else if (response.statusCode().isError) {
                        logErrorResponse(
                            response = response,
                            functionName = ::getUserInfo.name,
                            classLogger = logger,
                        ).then(response.createError())
                    } else {
                        response.bodyToMono<ExtendedUserResponse>()
                    }
                }
                .block() ?: throw RuntimeException("Could not get user info for $navIdent")
        }
    }

    @Retryable(
        excludes = [EnhetNotFoundException::class]
    )
    fun getUsersInEnhet(
        enhetsnummer: String,
    ): UsersResponse {
        return runWithTimingAndLogging {
            val token = "Bearer ${tokenUtil.getOnBehalfOfTokenWithKlageLookupScope()}"
            klageLookupWebClient.get()
                .uri("/enheter/$enhetsnummer/users")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    token,
                )
                .exchangeToMono { response ->
                    if (response.statusCode().value() == 404) {
                        logger.debug("Enhet $enhetsnummer not found")
                        Mono.error(EnhetNotFoundException("Enhet $enhetsnummer not found"))

                    } else if (response.statusCode().isError) {
                        logErrorResponse(
                            response = response,
                            functionName = ::getUsersInEnhet.name,
                            classLogger = logger,
                        )
                        response.createError()
                    } else {
                        response.bodyToMono<UsersResponse>()
                    }
                }
                .block() ?: throw RuntimeException("Could not get users information for enhet $enhetsnummer")
        }
    }

    fun <T> runWithTimingAndLogging(block: () -> T): T {
        val start = System.currentTimeMillis()
        try {
            return block.invoke()
        } finally {
            val end = System.currentTimeMillis()
            logger.debug("Time it took to call klage-lookup: ${end - start} millis")
        }
    }
}