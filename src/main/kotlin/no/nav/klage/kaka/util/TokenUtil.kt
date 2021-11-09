package no.nav.klage.kaka.util


import no.nav.klage.kaka.config.SecurityConfig
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Service

@Service
class TokenUtil(private val tokenValidationContextHolder: TokenValidationContextHolder) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    fun getAccessTokenFrontendSent(): String =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD).tokenAsString

    fun getIdent(): String =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.get("NAVident")?.toString()
            ?: throw RuntimeException("Ident not found in token")

    //NB! Returnerer objectId'er, ikke navn på gruppene!
    fun getRollerFromToken(): List<String> =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.getAsList("groups").orEmpty().toList()

    //Brukes ikke per nå:
    fun erMaskinTilMaskinToken(): Boolean {
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.allClaims?.forEach { securelogger.info("${it.key} - ${it.value}") }

        return getClaim("sub") == getClaim("oid")
    }

    private fun getClaim(name: String) =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.getStringClaim(name)
}